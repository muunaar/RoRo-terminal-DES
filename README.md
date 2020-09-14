# RoRo-Process-System-Simulation

This repository holds the code  source of a discrete event simulation system for a port terminal : Roll on Roll off terminal process.
Here in this version, the export process is simulated in order to imitate the behaviour of the RORO terminal.

### Description of the system simulated.
The process is initiated at the entry gate where the entity (vehicle) enters the terminal where the main gate operations are performed. After that, it is directed to the prevalidation phase, followed by the scanner
where security check is done. If the operation is performed successfully, the vehicle starts administrative procedure where the regulation export is involved.
Once the regulation is  finished, the vehicle heads to the export terminal where it waits for
 the RORO vessel's arrival. Finally, boarding is the final step before the vehicle leaves the terminal.
 
 ### Technologies
The project is built upon two major functional libraries for Scala [Cats](https://typelevel.org/cats/) and [Cats Effects](https://typelevel.org/cats-effect/) and [FS2](https://github.com/typelevel/fs2) library to model the controle flow as well as vehicles streaming.



##License 
This content of this project itself is not licensed under any licenses.

