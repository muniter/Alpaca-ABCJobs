import * as cdk from 'aws-cdk-lib';
import { Certificate } from 'aws-cdk-lib/aws-certificatemanager';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as elbv2 from 'aws-cdk-lib/aws-elasticloadbalancingv2';
import * as route53 from 'aws-cdk-lib/aws-route53';
import { IHostedZone } from 'aws-cdk-lib/aws-route53';
import { Construct } from 'constructs';

interface LoadBalancerStackProps extends cdk.StackProps {
  vpc: ec2.Vpc;
  servicesSecurityGroup: ec2.SecurityGroup;
  certificate: Certificate,
  hostedZone: IHostedZone,
}

export class LoadBalancerStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props: LoadBalancerStackProps) {
    super(scope, id, props);

    const alb = new elbv2.ApplicationLoadBalancer(this, 'ALB', {
      vpc: props.vpc,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PUBLIC
      },
      internetFacing: true,
      loadBalancerName: 'abc-alb'
    });

    // Create route53 record to point *.abc.muniter.link to the load balancer
    new route53.CnameRecord(this, 'ALBRoute53Cname', {
      zone: props.hostedZone,
      domainName: alb.loadBalancerDnsName,
      recordName: 'api.abc.muniter.link',
    });


    // Default target group for fallback purposes.
    const defaultTargetGroup = new elbv2.ApplicationTargetGroup(this, 'DefaultTargetGroup', {
      vpc: props.vpc,
      port: 80,
      targetType: elbv2.TargetType.IP,
    });

    // HTTP listener with a redirection action to HTTPS
    alb.addListener('HttpListener', {
      port: 80,
      defaultAction: elbv2.ListenerAction.redirect({
        protocol: 'HTTPS',
        port: '443',
      }),
    });

    // HTTPS listener
    const httpsListener = alb.addListener('HttpsListener', {
      port: 443,
      certificates: [props.certificate],
      defaultTargetGroups: [defaultTargetGroup],  // Associate the default target group
    });

    // Otput the loadBalancer ARN
    new cdk.CfnOutput(this, 'ALBArnOutput', {
      description: 'Load balancer ARN',
      value: alb.loadBalancerArn,
    })
    const gestionTargetGroup = new elbv2.ApplicationTargetGroup(this, 'GestionTargetGroup', {
      vpc: props.vpc,
      port: 80,
      targetType: elbv2.TargetType.IP,
      targetGroupName: 'gestion-evaluaciones',
      healthCheck: {
        protocol: elbv2.Protocol.HTTP,
        path: '/health',
        timeout: cdk.Duration.seconds(5),
        interval: cdk.Duration.seconds(30),
        unhealthyThresholdCount: 2,
        healthyThresholdCount: 2,
      },
    });

    // Add an action to the httpsListener to route traffic with path /gestion to the gestionTargetGroup
    httpsListener.addAction('RouteToGestion', {
      priority: 10,
      conditions: [elbv2.ListenerCondition.pathPatterns(['/gestion'])],
      action: elbv2.ListenerAction.forward([gestionTargetGroup])
    });

    new cdk.CfnOutput(this, 'GestionEvaluacionsTGOutput', {
      description: 'Gestion de evaluaciones TG ARN',
      value: gestionTargetGroup.targetGroupArn,
    });



  }
}
