
import * as cdk from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';
import { ApplicationListener, ILoadBalancerV2 } from 'aws-cdk-lib/aws-elasticloadbalancingv2';

interface ClusterStackProps extends cdk.StackProps {
  vpc: ec2.IVpc;
  loadBalancer: ILoadBalancerV2
  httpsListener: ApplicationListener,
}


export class ClusterStack extends cdk.Stack {
  cluster: ecs.ICluster;
  taskRole: iam.Role;
  taskExecutionRole: iam.Role;
  servicesSecurityGroup: ec2.SecurityGroup;

  constructor(scope: Construct, id: string, props: ClusterStackProps) {
    super(scope, id, props);


    // Create an ECS cluster
    this.cluster = new ecs.Cluster(this, 'Cluster', {
      clusterName: 'abc-cluster',
      containerInsights: true,
      defaultCloudMapNamespace: {
        name: 'abc-namespace'
      },
      enableFargateCapacityProviders: true,
      vpc: props.vpc
    })

    // Services security groups, needed when creating the services
    const servicesSecurityGroup = new ec2.SecurityGroup(this, 'ServicesSecurityGroup', {
      vpc: props.vpc,
      securityGroupName: 'abc-services-sg',
      description: 'Security group for services',
      allowAllOutbound: true
    });
    servicesSecurityGroup.connections.allowFromAnyIpv4(ec2.Port.tcp(80), 'Allow HTTP');
    this.servicesSecurityGroup = servicesSecurityGroup;

    // Task execution role (this is used by ECS itself)
    this.taskExecutionRole = new iam.Role(this, 'TaskExecutionRole', {
      roleName: 'abc-task-execution-role',
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonECSTaskExecutionRolePolicy'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonEC2ContainerServiceRole'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('SecretsManagerReadWrite'),
      ]
    })

    // Allow create log groups
    this.taskExecutionRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'logs:CreateLogGroup',
      ],
      resources: ['*']
    }));

    // Task role (this is used by the containers, the services we write)
    this.taskRole = new iam.Role(this, 'TaskRole', {
      roleName: 'abc-task-role',
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonS3FullAccess'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonSQSFullAccess'),
      ]
    });

    this.taskRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        "ssmmessages:CreateControlChannel",
        "ssmmessages:CreateDataChannel",
        "ssmmessages:OpenControlChannel",
        "ssmmessages:OpenDataChannel"
      ],
      resources: ["*"]
    }));
  }
}
