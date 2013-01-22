# remote-console

A simple wrapper on top of [JSch](http://www.jcraft.com/jsch/) for a simpler interface. 
Allowing running remote commands, uploading and downloading files
## Download and use
Use maven
```xml
<repository>
    <id>deck-repo</id>
    <url>https://github.com/JesperTerkelsen/deck-mvn-repo/raw/master/releases</url>
</repository>
<dependency>
    <groupId>dk.deck.remote-console</groupId>
    <artifactId>remote-console</artifactId>
    <version>1.1.3</version>
</dependency>
```

## How to use
### Connect

```java
JSch jsch = new JSch();
RemoteConsole console = new SshRemoteConsole(jsch);
console.setHost("www.example.com");
console.setUser("joe");
console.setIdentityFile(new File(".ssh/id_rsa")); // Optional if you use ssh keys
console.setUserInfoProvider(...); // This interface will provide the system with passwords/passphrases, see below
console.connect();
// Do some work on the server, see below
// Commands can be run in parallel because of the SSH session/channel protocol.  
console.disconnect();
```

Simple implementation of UserInfoProvider for getting passwords, plug your own UI in here.
```java
remote.setUserInfoProvider(new UserInfoProvider() { // UserInfoProvider is a interface used by RemoteConsole
            public UserInfo getUserInfo() { // UserInfo is a class from the JSch API 
                return new LoggingUserInfo(){ // LoggingUserInfo is an implementation that just logs the requests given.
                    public String getPassword() {
                        return "secret";
                    }
                    public boolean promptPassword(String password) {
                        return true;
                    }
                };
            }
        });
```
### Run remote command 
Simplest form - Standard out is return in the string, an exception will be thrown if the exit value is not zero.
```java
String result = console.executeCommand("script.sh");
```
More feedback - Returns a object to get the value from.
```java
CommandResult result = console.executeCommandResult("script.sh");
String output = result.getOutput(); // From std out
String errorOutput = result.getErrorOutput(); // From std error
int exitCode = result.getExitCode(); // exit code
boolean ok = result.isOk(); // Was the return value zero
```
### Upload file
```java
URL localFile = getClass().getResource("testfile.txt");
console.uploadFile(localFile, "testfile.txt");
```
### Download file
```java
OutputStream localFile = new FileOutputStream("testfile.txt");
console.downloadFile("testfile.txt", localFile);
```