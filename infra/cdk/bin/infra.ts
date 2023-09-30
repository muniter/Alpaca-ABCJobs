#!/usr/bin/env node
import 'source-map-support/register';
import * as cdk from 'aws-cdk-lib';
import { InfraStack } from '../lib/infra-stack';
import { LoadBalancerStack } from '../lib/load-balancer-stack';
import { DomainStack } from '../lib/domain-stack';
import GithubActionsStack from '../lib/github-actions-stack';
import { ClusterStack } from '../lib/cluster-stack';
import { DataStack } from '../lib/data-stack';

const app = new cdk.App();

const baseProps: cdk.StackProps = {
  env: {
    account: '428011609647',
    region: 'us-east-1',
  },
};

const infraStack = new InfraStack(app, 'InfraStack', baseProps);
const domainStack = new DomainStack(app, 'DomainStack', { ...baseProps, vpc: infraStack.vpc });
const loadBalancerStack = new LoadBalancerStack(app, 'LoadBalancerStack', {
  ...baseProps,
  vpc: infraStack.vpc,
  certificate: domainStack.certificate,
  hostedZone: domainStack.hostedZone,
});
const clusterStack = new ClusterStack(app, 'ClusterStack', {
  ...baseProps,
  vpc: infraStack.vpc,
  loadBalancer: loadBalancerStack.loadBalancer,
  httpsListener: loadBalancerStack.httpsListener,
})
new GithubActionsStack(app, 'GithubActionsStack', {
  ...baseProps,
  // clusterExecutionRole: clusterStack.taskExecutionRole,
  // clusterTaskRole: clusterStack.taskRole,
});

new DataStack(app, 'DataStack', {
  ...baseProps,
  vpc: infraStack.vpc,
  certificate: domainStack.certificate,
  hostedZone: domainStack.hostedZone,
});
