# EmailBillSender-daemon
Email sender service to replace BIP

# Info
This is the netbeans project folder. Clone it using netbeans for easy edit / compilation

# Cloning
After cloning, create a config folder outside your project folder. eg:
/<parent folder of your project folder>
- EmailBillSender-daemon/ <your project folder>
- - src/    <these are the things that you cloned inside the EmailBillSender-daemon folder>
- - config/
- - nbproject/
- config/  <this is the folder that you need to create>

Then, copy the configs inside the project/config folder into the new config folder that you have created just now
And change the content accordingly

# Deployment step
Compile the project using netbeans and deploy the content of 'dist' folder into desired server.
If possible, please follow the following folder structure at the server:

/<base folder>
- lib/  <the content of dist will go here. usually with the extra 'lib' folder in it>
- config/  <the config file should go here>
- log/  <if necessary>

Change the config accordingly
