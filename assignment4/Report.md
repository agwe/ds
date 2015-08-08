# DFS design

**Assumption 1:** dfs works only with text files. Because even in real system you need external application to open any files except text files.

**Assumption 2:** files are stored in memory, not on the hard disk.

**Assumption3**: There is predefined list of servers in server list passing as a parameter to the client, so no new servers can join the system in runtime.

### There are 3 types of paticipants in the system:
1. Client - get user commands from the cmd and parse them (validate, interpret to decide which method from master to run).
2. Master - route requests to the servers, choose server via roundrobin, store filesystem tree
3. Server(s) - store files

##Client

Client is created from cmd with list of server adresses in txt file and local address (both adresses are in format "ip:port")
Then client is ready to exeute user commands.

### Possible user commands

**cat** - open file, if it exists in current directory; create file otherwise

**rm** - delete file, if it exists in current directory

**mkdir** - create folder, if it doesn't exist in current directory

**rmdir** - delete folder, if it exists in current directory

**cd** - change directory, if it exists in current directory or a parent of current directory

**ls** - list files and folders in directory

**stat** - get file or directory info

##Master
Master is a part of cient. It owns methods to work with server and file structure.

**Master constructor.** Creates client's socket, parse the file with server addresses and created socke for them.

**ChooseServer** Has routing functions for choosing server (choosing the one in order (current server position is stored by means of iterator for sockets array), getting next from the server sockets list, if the prevous one was unavailable and count if all were tried in one round, whish means that all servers are unavailable)

**StoreData** Send data to chose in choose server method server.

**GetData** Retrieve data with specified hash from the server, which address is stored in object pathToServer field.

Other methods like changeDirectory, openFile etc. contains logic to process corresponding user commands.

###Filesystem structure

Structure is stored in the tree. Src contains 3 classes TreeNode (abstract), DirectoryNode for disrectories and FieNode for storing files correspondingly. 

**TreeNode** contains basic properties as name, hash (to uniquily identify files) and parent node.

**FileNode** moreover contains path to the server, where file is stored

**DirectoryNode** moreover contains directory children

##Server##
Server has to be run separately with parameter "ip:port" in order to create server socket. Server parse input parameter and in case of success start server thread wich process requests from master: 
1. Store file
2. Delete file
3. Return file

All files are stored without any hierarchy with their hash as names instead of their real names (in order to avoid duplication). 

##DSS Taxonomy##
Solution represents global centrilized architecture in trusted environment

##Running the solution##
1. Compile the program and run from target:
2. Run server(s) via console java -cp dfs-1.0-SNAPSHOT.jar edu.innopolis.Server 127.0.0.1:4446 (don't forget to change port number. Note, that the addresses specified must be declared in servers.txt)
2. Run client java -cp dfs-1.0-SNAPSHOT.jar edu.innopolis.Client 127.0.0.1:4445 servers



