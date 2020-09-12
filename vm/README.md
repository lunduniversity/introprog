# How to create the pgk vm

## Prepare the base machine

* Install Oracle virtual box current version 6.1
https://www.virtualbox.org/

* Download the latest Ubuntu 20.04 iso
http://releases.ubuntu.com/20.04/

* In virtualbox create a NEW machine 
  - 4196 GB RAM
  - 25GB virtual dynamic disk
  - Linux Ubuntu

* Start the machine and insert the iso

* Do a minimal install to minimize file size, include extra drivers

* Restart the machine

* run Software Updater and apply all updates 

* Restart the machine

* power off the machine

* export to an .ova file called `pgk-vm2020-base.ova` or similar by selecting menu Machine -> Export OCI... and 
  - select Format "Open Virt ... 1.0"
  - MAC addr policy "Include only NAT except MAC"
  - tick Write Manifest file
  - untic include iso image

## Clone the base

Clone in virtual box by selecting Clone... 
- Name: pgk-vm2020
- unticked "Keep Disk Names"
- unticked "keep hardware UUID"
- "Full Clone"
- 

## Some extra tweaking

* make terminal use Ctrl+C and Ctrl+V in terminal->Edit->Preferences->Shortcuts

* install this

```
sudo apt update
sudo apt dist-upgrade
sudo apt install curl
sudo apt install ubuntu-restricted-extras
sudo apt install gnome-tweak-tool
```

## Install the course tools

```
mkdir -p bin
cd bin
wget -O cs https://git.io/coursier-cli-linux && chmod +x cs && ./cs setup
```
Then reboot. 

Then install VS Code:
```
sudo snap install code --classic
```

* Fire up visual studio code and install extension "Scala (Metals)"

* Install latest from http://www.kogics.net/kojo-download 

## Export to .ova

* export an .ova file called `pgk-vm2020-base.ova` or similar by selecting menu Machine -> Export OCI... and 
  - select Format "Open Virt ... 2.0"
  - MAC addr policy "Include only NAT except MAC"
  - tick Write Manifest file
  - untic include iso image

## Upload to file server
This can take more than an hour:
```
scp ~/Documents/pgk-vm2020.ova $LUCATID@web.cs.lth.se:/Websites/Fileadmin/pgk/.
```
