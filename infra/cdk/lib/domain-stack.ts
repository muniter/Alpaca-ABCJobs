import * as cdk from 'aws-cdk-lib';
import * as ec2 from 'aws-cdk-lib/aws-ec2';
import * as route53 from 'aws-cdk-lib/aws-route53';
import * as acm from 'aws-cdk-lib/aws-certificatemanager';
import { Construct } from 'constructs';
// import * as sqs from 'aws-cdk-lib/aws-sqs';

interface DomainStackProps extends cdk.StackProps {
  vpc: ec2.Vpc;
}

export class DomainStack extends cdk.Stack {
  public readonly certificate: acm.Certificate;
  public readonly hostedZone: route53.IHostedZone;

  constructor(scope: Construct, id: string, props: DomainStackProps) {
    super(scope, id, props);
    this.hostedZone = route53.HostedZone.fromLookup(this, 'HostedZone', {
      domainName: 'muniter.link',
      privateZone: false,
    });

    this.certificate = new acm.Certificate(this, 'Certificate', {
      domainName: '*.abc.muniter.link',
      validation: acm.CertificateValidation.fromDns(this.hostedZone),
    });
  }
}


