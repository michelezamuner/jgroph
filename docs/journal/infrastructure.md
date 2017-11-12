# Infrastructure


## Development environment

A common category of problems that we face when developing applications includes differences of behavior between local
and remote (e.g. production) environments. To protect ourselves from this kind of issues, we want to setup a development
environment that resembles as closely as possible the remote ones. The easiest method to achieve this, given the nature
of this project, is using Vagrant virtual machines.

Thus, we want to create a virtual environment with certain characteristics, whose configuration will be committed along
with the project's code, meaning a `Vagrantfile` and a provisioning script.

The production environment will be an OpenShift "Do It Yourself" gear, since OpenShift doesn't provide a gear with
Jetty, yet. Here lies our first compromise: OpenShift runs on RedHat systems, and thus I'd first thought to use a CentOS
or Fedora Vagrant box; however, after investing some time into trying them, I gave up, due to a lot of little issues
that I found using those boxes, that I didn't experience with the Ubuntu ones, with which I'm more familiar. Example of
issues I found were:
- being unable to make NFS synced folders work
- synced folders not automatically updating when changing files in the host environment, due to some problem with the
rsync automatic update
- weird problems during the machine boot due to Vagrant timing out when trying to connect to the machine, for which some
additional configuration needed to be added to the Vagrantfile, that didn't work consistently anyway.

For these reasons, I opted for a `ubuntu/trusty64` box, that didn't give any of the previously mentioned problems.

Speaking of NFS synced folders, at first I thought it would be cool to use them, and they worked just fine with the
Ubuntu box. However, in the end I discarded this option, and get back to the default `vboxsf`, because if I ever had to
continue the development on a Windows host, I imagine NFS folders to be quite hard to setup there, and I want to cut
down operations work as much as possible. Also, the speed benefit is not that important, at least in the beginning.

I'd like to keep the memory of the virtual machine at a minimum, to be able to immediately spot performance issues.
However, this can quite likely be a stupid choice because it will slow down infrastructure operations like provisioning,
JVM boot, code compilation, etc., that happen almost never on the production machine, but quite often on the development
one. However, since the laptop I use to work on personal projects has quite low specs, I still prefer to keep the VM
memory low. For now I settled to `512` MB.

However, there's a way to improve the usage of swap space by the guest, which consists just in installing the
`swapspace` package inside the guest:

```bash
sudo apt-get install swapspace
```

and then re-run the provisioning. This will spawn a daemon in the guest, that will keep monitoring the memory usage, and
if it finds that there's not enough memory, it'll use disk space for it. Check also [this link](https://superuser.com/questions/1058491/is-it-possible-to-create-a-swap-file-for-a-linux-guest-vm-managed-by-vagrant).


## Provisioning

Now we get to the VM provisioning. On more serious setups, I would separate everything related to the infrastructure
from the project repository. However, in this case it's more important for me to keep everything in one place, since
personal projects are very messy and volatile in nature. Thus, I'm including a `provision.sh` Bash script in the
repository. The provisioning procedure should setup the Vagrant box so that it resembles as closely as possible the
RedHat server, at least for the features we are interested in:
- OpensShift comes with JDK 8 pre-installed at `/etc/alternatives/java_sdk_8_0`, so I need to download JDK 8 from Oracle
and install it in that location
- OpenShift comes with a bunch of environmental variables, in particular those containing the paths of the user
directories, in addition to certain values such as the IP address and port that we are allowed to use for our Web
server. These need to be setup with the same names in the Ubuntu machine: to do this I just add a
`/etc/profile.d/custom.sh` script, containing variables definitions.

Additionally, I added two custom variables containing the starting and maximum memory to be used when launching the JVM.
These are calculated as half and three quarters of the current machine memory, and will be used in the server start
script. In the production environment, of course, these won't be defined, but in that case we will use default values.
This is nice to have to keep the JVM at a controlled size.


## Application start script

The next step is configuring the scripts that OpenShift will use on the production server (and that will also be used
locally) during the startup/shutdown procedures. Taking inspiration from the [Jetty on OpenShift repository](https://github.com/openshift-quickstart/jetty-openshift-quickstart)
I first prepared a `start` and `stop` scripts. However, only later I discovered that for some reason the `stop` script
wasn't properly destroying the running server process in the production server, that would be still be present during
the following startup, preventing the new server instance to be created due to conflicting IP address and port. Thus, I
moved the code to kill the existing server process in the startup script as well.

Another difference with the suggested setup is related to the use of Maven. While it's true that OpenShift comes with
Maven pre-installed, that application is using the JDK 7 installed on OpenShift, and I couldn't figure out how to make
it use JDK 8. The result was that, using the provided Maven, I wasn't able to compile Java 8 code. The solution was to
download a local copy of Maven, which would use the `$JAVA_HOME` that I defined in the startup script, pointing to the
provided JDK 8.


## Jetty setup

The Jetty version I downloaded (compared to the Jetty Openshift repo one), is shipped with a weird initial
configuration, which basically doesn't work, meaning that you can't even serve static files, let alone servlets. If you
go into the demo folder and launch Jetty from there, it works fine, but to make it work from the main Jetty folder you
have to rebuild the configuration. In particular, what I had to do was deleting `start.ini`, and first launch Jetty with
the options `--create-startd` and `--add-to-start=jsp,http,webapp,deploy` to build a proper configuration. At that
point, launching Jetty again as a server, it was working fine.