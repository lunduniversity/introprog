# How to create the pgk vm

## Prepare the base machine

* Install Oracle virtual box 
https://www.virtualbox.org/

* Download the Ubuntu 18.04.3 iso
http://releases.ubuntu.com/18.04/

* In virtualbox create a NEW machine 
  - 2048 GB RAM
  - 25GB virtual dynamic disk
  - Linux Ubuntu

* Start the machine and insert the iso

* Do a minimal install to minimize file size, include extra drivers

* Restart the machine

* run Software Updater and apply all updates 

* Restart the machine

* power off the machine

* export to an .ova file called `pgk-vm2019-base.ova` or similar by selecting menu Machine -> Export OCI... and 
  - select Format "Open Virt ... 1.0"
  - MAC addr policy "Include only NAT except MAC"
  - tick Write Manifest file
  - untic include iso image

## Clone the base

Clone in virtual box by selecting Clone... 
- Name: pgk-vm2019
- unticked "Keep Disk Names"
- unticked "keep hardware UUID"
- "Full Clone"
- 

## Some extra tweaking

* make terminal use Ctrl+C and Ctrl+V in terminal->Edit->Preferences->Shortcuts

* install this

```
sudo apt install ubuntu-restricted-extras
gsettings set org.gnome.shell.extensions.dash-to-dock click-action 'minimize'
sudo apt install gnome-tweak-tool
```

## Install the course tools

```
sudo apt install openjdk-8-jdk

cd ~/Downloads
wget https://downloads.lightbend.com/scala/2.12.9/scala-2.12.9.deb
sudo dpkg -i scala-2.12.9.deb

echo "deb https://dl.bintray.com/sbt/debian /" | sudo tee -a /etc/apt/sources.list.d/sbt.list
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
sudo apt-get update && sudo apt-get install sbt
```

* Goto https://code.visualstudio.com/docs/?dv=linux64_deb and download deb and install by double click

* Fire up visual studio code and install "Metals (Scala)"

* Install latest from http://www.kogics.net/kojo-download 

* Install latest from https://github.com/JetBrains/intellij-scala-bundle/releases 

* Fix launchers in ~/.local/share/applications 

## Export to .ova

* export an .ova file called `pgk-vm2019-base.ova` or similar by selecting menu Machine -> Export OCI... and 
  - select Format "Open Virt ... 1.0"
  - MAC addr policy "Include only NAT except MAC"
  - tick Write Manifest file
  - untic include iso image

## Upload to file server
This can take more than an hour:
```
scp ~/Documents/pgk-vm2019.ova $LUCATID@web.cs.lth.se:/Websites/Fileadmin/pgk/.
```
