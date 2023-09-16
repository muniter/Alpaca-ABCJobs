import * as cdk from 'aws-cdk-lib';
import { Certificate } from 'aws-cdk-lib/aws-certificatemanager';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as elb from 'aws-cdk-lib/aws-elasticloadbalancingv2';
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

    const alb = new elb.ApplicationLoadBalancer(this, 'ALB', {
      vpc: props.vpc,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PUBLIC
      },
      internetFacing: true,
      loadBalancerName: 'abc-alb'
    });

    // Default target group for fallback purposes.
    const defaultTargetGroup = new elb.ApplicationTargetGroup(this, 'DefaultTargetGroup', {
      vpc: props.vpc,
      port: 80,
      targetType: elb.TargetType.IP,
    });

    // HTTP listener with a redirection action to HTTPS
    alb.addListener('HttpListener', {
      port: 80,
      defaultAction: elb.ListenerAction.redirect({
        protocol: 'HTTPS',
        port: '443',
      }),
    });

    // HTTPS listener
    alb.addListener('HttpsListener', {
      port: 443,
      certificates: [props.certificate],
      defaultTargetGroups: [defaultTargetGroup],  // Associate the default target group
    });

    // Otput the loadBalancer ARN
    new cdk.CfnOutput(this, 'ALBArnOutput', {
      description: 'Load balancer ARN',
      value: alb.loadBalancerArn,
    })

    // Create route53 record to point *.abc.muniter.link to the load balancer
    const record = new route53.CnameRecord(this, 'ALBRoute53Cname', {
      zone: props.hostedZone,
      domainName: alb.loadBalancerDnsName,
      recordName: 'api.abc.muniter.link',
    });

  }
}
