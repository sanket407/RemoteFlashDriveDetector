# RemoteFlashDriveDetector
Java Client Server Application which detects insertion and removal of Flash Drives in Client Computers Across a LAN !

Tested successfully on Windows 7 and Ubuntu 16.04

Steps to Run Client:
1. Open RemoteFlashDriveDetector/remoteflashdrivedetector-client/src/main/resources/settings.properties
2. Set operating system to either "ubuntu" or "windows" depending on the OS on client machine.
3. Set port
4. Go the RemoteFlashDriveDetector/remoteflashdrivedetector-client/
5. Run from terminal by using -> mvn exec:java

Steps to Run Server:
1. Open RemoteFlashDriveDetector/remoteflashdrivedetector-server/src/main/resources/settings.properties
2. Set ip to the same ip as your network interface on the server machine.
3. Set port 
4. Go the RemoteFlashDriveDetector/remoteflashdrivedetector-server/
5. Run from terminal by using -> mvn exec:java

IMPORTANT - **Port on both client and server should be same to enable multicasting to work**
