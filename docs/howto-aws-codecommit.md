# How To access AWS CodeCommit repositories

## If you don't use AWS Organizations

You can follow the [AWS documentation](https://docs.aws.amazon.com/codecommit/latest/userguide/setting-up-gc.html).

## If you you use AWS Organizations
If you are using [AWS Organizations](https://aws.amazon.com/organizations/), then you are probably using profiles and roles to access resources.

The dev contains already has [git-remote-codecommit](https://docs.aws.amazon.com/codecommit/latest/userguide/setting-up-git-remote-codecommit.html).

```
git clone codecommit://<YOUR-ROLE-NAME>@<CODECOMMIT-REPO-NAME>
```

For example:
```
git clone codecommit://admin-development@photo-blog-app
```