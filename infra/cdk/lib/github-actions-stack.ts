import * as cdk from 'aws-cdk-lib';
import * as iam from 'aws-cdk-lib/aws-iam';
import { Construct } from 'constructs';

interface GithubActionsStackProps extends cdk.StackProps {
  clusterExecutionRole?: iam.IRole;
  clusterTaskRole?: iam.IRole;
}

export default class GithubActionsStack extends cdk.Stack {

  constructor(scope: Construct, id: string, props: GithubActionsStackProps) {
    super(scope, id, props);

    const federatedPrincipal = new iam.FederatedPrincipal(`arn:aws:iam::${cdk.Aws.ACCOUNT_ID}:oidc-provider/token.actions.githubusercontent.com`, {
      ["StringLike"]: {
        "token.actions.githubusercontent.com:sub": "repo:muniter/Alpaca-ABCJobs:*"
      },
      ["ForAllValues:StringEquals"]: {
        "token.actions.githubusercontent.com:iss": "https://token.actions.githubusercontent.com",
        "token.actions.githubusercontent.com:aud": "sts.amazonaws.com"
      }
    }, "sts:AssumeRoleWithWebIdentity")

    const role = new iam.Role(this, 'Role', {
      roleName: 'github-actions-alpaca-role',
      assumedBy: federatedPrincipal,
    })

    const policy = new iam.Policy(this, 'Policy', {
      policyName: 'github-actions-alpaca-policy',
      statements: [
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          resources: ['*'],
          actions: [
            'ecr:BatchCheckLayerAvailability',
            'ecr:BatchGetImage',
            'ecr:CompleteLayerUpload',
            'ecr:GetAuthorizationToken',
            'ecr:InitiateLayerUpload',
            'ecr:PutImage',
            'ecr:UploadLayerPart',
          ]
        }),
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          resources: ['*'],
          actions: [
            'ecs:RegisterTaskDefinition',
            'ecs:DescribeServices',
            'ecs:UpdateService',
          ]
        }),
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          resources: ['*'],
          actions: [
            "codedeploy:GetDeploymentGroup",
            "codedeploy:CreateDeployment",
            "codedeploy:GetDeployment",
            "codedeploy:GetDeploymentConfig",
            "codedeploy:RegisterApplicationRevision"
          ]
        }),
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          resources: ['arn:aws:s3:::abc-*'],
          actions: [
            's3:*',
            's3-object-lambda:*',
          ],
        }),
        new iam.PolicyStatement({
          effect: iam.Effect.ALLOW,
          actions: [
            'iam:PassRole',
          ],
          resources: [
            'arn:aws:iam::*:role/abc-task-*',
          ]
        }),
      ],
    });
    policy.attachToRole(role);
  }
}

