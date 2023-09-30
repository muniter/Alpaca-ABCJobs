import * as cdk from 'aws-cdk-lib';
import { ICertificate } from 'aws-cdk-lib/aws-certificatemanager';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as rds from 'aws-cdk-lib/aws-rds';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as s3 from 'aws-cdk-lib/aws-s3';
import { Construct } from 'constructs';
import * as route53 from 'aws-cdk-lib/aws-route53';
import * as route53_targets from 'aws-cdk-lib/aws-route53-targets';
import * as cloudfront from 'aws-cdk-lib/aws-cloudfront';
import * as cloudfront_origins from 'aws-cdk-lib/aws-cloudfront-origins';

interface DataStackProps extends cdk.StackProps {
  vpc: ec2.IVpc;
  certificate: ICertificate,
  hostedZone: route53.IHostedZone,
}



export class DataStack extends cdk.Stack {
  vpc: ec2.Vpc;

  constructor(scope: Construct, id: string, props: DataStackProps) {
    super(scope, id, props);

    site(this, props);
    // Expensive don't deploy for now
    // database(this, 'Database', props);
  }
}

function site(scope: Construct, props: DataStackProps) {

  const cloudfrontOAI = new cloudfront.OriginAccessIdentity(scope, 'CloudFrontOAI', {
    comment: 'OAI for the abc jobs website'
  });

  const siteBucket = new s3.Bucket(scope, 'WebsiteBucket', {
    bucketName: 'abc-jobs-web',
    accessControl: s3.BucketAccessControl.PRIVATE,
  });

  siteBucket.addToResourcePolicy(new iam.PolicyStatement({
    actions: ['s3:GetObject'],
    resources: [siteBucket.arnForObjects('*')],
    principals: [new iam.CanonicalUserPrincipal(cloudfrontOAI.cloudFrontOriginAccessIdentityS3CanonicalUserId)],
  }))

  const distribution = new cloudfront.Distribution(scope, 'Distribution', {
    comment: 'Distribution for the abc jobs website',
    certificate: props.certificate,
    domainNames: ['jobs.abc.muniter.link'],
    defaultRootObject: 'index.html',
    errorResponses: [
      {
        httpStatus: 403,
        responseHttpStatus: 403,
        responsePagePath: '/error.html',
      }
    ],
    defaultBehavior: {
      origin: new cloudfront_origins.S3Origin(siteBucket, {
        originAccessIdentity: cloudfrontOAI,
      }),
      compress: true,
      allowedMethods: cloudfront.AllowedMethods.ALLOW_GET_HEAD_OPTIONS,
      viewerProtocolPolicy: cloudfront.ViewerProtocolPolicy.REDIRECT_TO_HTTPS,
    }
  })

  new route53.ARecord(scope, 'SiteAliasRecord', {
    zone: props.hostedZone,
    recordName: 'jobs.abc.muniter.link',
    target: route53.RecordTarget.fromAlias(new route53_targets.CloudFrontTarget(distribution)),
  });
}

function database(scope: Construct, id: string, props: DataStackProps) {
  // Create RDS instance
  const rdsInstance = new rds.DatabaseInstance(scope, id, {
    engine: rds.DatabaseInstanceEngine.postgres({ version: rds.PostgresEngineVersion.VER_15 }),
    instanceType: ec2.InstanceType.of(ec2.InstanceClass.T3, ec2.InstanceSize.MICRO),
    databaseName: 'abc',
    vpc: props.vpc,
    vpcSubnets: {
      subnetType: ec2.SubnetType.PUBLIC
    },
    credentials: rds.Credentials.fromGeneratedSecret('abc', {
      secretName: 'abc-rds-secret',
    }),
  });
  // Allow connection to the database
  rdsInstance.connections.allowFrom(ec2.Peer.anyIpv4(), ec2.Port.tcp(5432), 'Allow internet to connect to RDS');

}
