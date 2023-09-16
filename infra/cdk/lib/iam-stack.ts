import * as cdk from 'aws-cdk-lib';
import * as iam from 'aws-cdk-lib/aws-iam';

export class IAMStack extends cdk.Stack {
  constructor(scope: cdk.Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    // Create IAM role with ECR & ECS full access
    const role = new iam.Role(this, 'MyECRECSRole', {
      assumedBy: new iam.ServicePrincipal('ec2.amazonaws.com'),  // Assuming this role will be used with EC2; adjust if needed
      description: 'Role that grants ECR and ECS full access.',
    });

    role.addManagedPolicy(iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonEC2ContainerRegistryFullAccess'));
    role.addManagedPolicy(iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonECS_FullAccess'));

    // Create IAM user that can assume the role
    const user = new iam.User(this, 'MyECRECSUser');

    const assumeRolePolicy = new iam.PolicyStatement({
      effect: iam.Effect.ALLOW,
      actions: ['sts:AssumeRole'],
      resources: [role.roleArn]
    });

    user.addToPolicy(assumeRolePolicy);
  }
}
