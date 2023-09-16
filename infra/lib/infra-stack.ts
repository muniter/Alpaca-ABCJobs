import * as cdk from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as ecs from 'aws-cdk-lib/aws-ecs';
import * as rds from 'aws-cdk-lib/aws-rds';
import * as ecr from 'aws-cdk-lib/aws-ecr';
import { Construct } from 'constructs';
// import * as sqs from 'aws-cdk-lib/aws-sqs';

export class InfraStack extends cdk.Stack {
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

    // Create RDS instance
    const rdsInstance = new rds.DatabaseInstance(this, 'RDSInstance', {
      engine: rds.DatabaseInstanceEngine.postgres({ version: rds.PostgresEngineVersion.VER_15 }),
      instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.MICRO),
      databaseName: 'abc',
      vpc,
      vpcSubnets: {
        subnetType: ec2.SubnetType.PUBLIC
      },
    });

    // Allow connection to the database
    rdsInstance.connections.allowFrom(ec2.Peer.anyIpv4(), ec2.Port.tcp(5432), 'Allow internet to connect to RDS');

    // Create ecr repositories
    const ecrGestion = new ecr.Repository(this, 'ECRRepositoryGestionEvaluaciones', {
      repositoryName: 'abc-gestion-evaluciones',
    });
    ecrGestion.addLifecycleRule({ maxImageCount: 10 });
  }
}
