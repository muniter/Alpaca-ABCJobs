import * as cdk from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as rds from 'aws-cdk-lib/aws-rds';
import * as ecr from 'aws-cdk-lib/aws-ecr';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';
// import * as sqs from 'aws-cdk-lib/aws-sqs';

export class InfraStack extends cdk.Stack {
  vpc: ec2.Vpc;
  servicesSecurityGroup: ec2.SecurityGroup;

  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create VPC with two AZ public subnets
    const vpc = new ec2.Vpc(this, 'MyVpc', {
      vpcName: 'abc-vpc',
      maxAzs: 2, // Default is all AZs in the region
      subnetConfiguration: [{
        subnetType: ec2.SubnetType.PUBLIC,
        name: 'Public',
        cidrMask: 24
      }]
    });
    this.vpc = vpc;
    // Output public subnets
    new cdk.CfnOutput(this, 'PublicSubnet1', {
      description: 'Public subnet 1',
      value: vpc.publicSubnets[0].subnetId
    });
    new cdk.CfnOutput(this, 'PublicSubnet2', {
      description: 'Public subnet 2',
      value: vpc.publicSubnets[1].subnetId
    });

    // Create an ECS cluster
    const cluster = new ecs.Cluster(this, 'MyCluster', {
      clusterName: 'abc-cluster',
      containerInsights: true,
      defaultCloudMapNamespace: {
        name: 'abc-namespace'
      },
      enableFargateCapacityProviders: true,
      vpc: vpc
    })

    // Services security groups, needed when creating the services
    const servicesSecurityGroup = new ec2.SecurityGroup(this, 'ServicesSecurityGroup', {
      vpc,
      securityGroupName: 'abc-services-sg',
      description: 'Security group for services',
      allowAllOutbound: true
    });
    servicesSecurityGroup.connections.allowFromAnyIpv4(ec2.Port.tcp(80), 'Allow HTTP');
    this.servicesSecurityGroup = servicesSecurityGroup;
    // Output the security group id
    new cdk.CfnOutput(this, 'ServicesSecurityGroupId', {
      description: 'Services security group id',
      value: servicesSecurityGroup.securityGroupId
    });

    // Create RDS instance
    const rdsInstance = new rds.DatabaseInstance(this, 'RDSInstance', {
      engine: rds.DatabaseInstanceEngine.postgres({ version: rds.PostgresEngineVersion.VER_15 }),
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.MICRO),
      databaseName: 'abc',
      vpc,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PUBLIC
      },
      credentials: rds.Credentials.fromGeneratedSecret('abc', {
        secretName: 'abc-rds-secret',
      }),
    });

    // Output the secret ARN
    new cdk.CfnOutput(this, 'RDSSecretArn', {
      description: 'RDS secret arn',
      value: rdsInstance.secret?.secretArn || ''
    });


    // Allow connection to the database
    rdsInstance.connections.allowFrom(ec2.Peer.anyIpv4(), ec2.Port.tcp(5432), 'Allow internet to connect to RDS');

    // Create ecr repositories
    const ecrGestion = new ecr.Repository(this, 'ECRRepositoryGestionEvaluaciones', {
      repositoryName: 'abc-gestion-evaluaciones',
    });
    ecrGestion.addLifecycleRule({ maxImageCount: 10 });

    // Task execution role (this is used by ECS itself)
    const taskExecutionRole = new iam.Role(this, 'TaskExecutionRole', {
      roleName: 'abc-task-execution-role',
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonECSTaskExecutionRolePolicy'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AmazonEC2ContainerServiceRole'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('SecretsManagerReadWrite'),
      ]
    });
    // Allow create log groups
    taskExecutionRole.addToPolicy(new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: [
        'logs:CreateLogGroup',
      ],
      resources: ['*']
    }));
    // Output
    new cdk.CfnOutput(this, 'TaskExecutionRoleArn', {
      description: 'Task execution role arn',
      value: taskExecutionRole.roleArn
    });

    // Task role (this is used by the containers, the services we write)
    const taskRole = new iam.Role(this, 'TaskRole', {
      roleName: 'abc-task-role',
      assumedBy: new iam.ServicePrincipal('ecs-tasks.amazonaws.com'),
      managedPolicies: [
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonS3FullAccess'),
        iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonSQSFullAccess'),
      ]
    });
    // Output
    new cdk.CfnOutput(this, 'TaskRoleArn', {
      description: 'Task role arn',
      value: taskRole.roleArn
    });

    // Usuario para integración continua
    const user = new iam.User(this, 'GithubActionsUser');
    user.addManagedPolicy(iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonEC2ContainerRegistryFullAccess'));
    user.addManagedPolicy(iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonECS_FullAccess'));
  }
}
