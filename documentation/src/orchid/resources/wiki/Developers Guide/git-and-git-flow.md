---
---

To participate to the development of 2P-Kt, the usage of [Git](https://git-scm.com/) is **mandatory**.
In particular, the Gradle script adopted by this project is configured in such a way to require a properly 
configured version of Git to be installed on the hosting system.

You can ensure Git is properly installed on your system by running
```bash
git --version 

# should output a line such as:
# git version 2.20.0.XXX
``` 

### Git Flow

[Git Flow](https://danielkummer.github.io/git-flow-cheatsheet) is essentially a principled way of using Git,
plus a command line tool enforcing it.
In particular, it is the _preferred_ way of using Git within the scope of this project, regardless of whether developers
leverage on the `git-flow` command line tool.

In particular, within the 2P-Kt project, the following conventions on branch names and commits hold:
![GitFlow example]({{ site.baseUrl }}/assets/media/git-flow.png)

## Useful resources

If you are not practical with Git, please consider reading the following resources:
1. [Pianini, Danilo. _"Productive parallel teamwork: Decentralized Version Control Systems"_](https://www.slideshare.net/DanySK/productive-parallel-teamwork-decentralized-version-control-systems)
0. [Atlassian. _"Gitflow Workflow"_](https://www.atlassian.com/git/tutorials/comparing-workflows/gitflow-workflow)
 