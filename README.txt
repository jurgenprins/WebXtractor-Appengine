WebXtractor-Appengine is a java implementation based on the concepts
in WebXtractor-PHP library.

WebXtractor-Appengine uses:

 1) Google Appengine
    * JDO persistence
    * TaskQueues
    
 2) Spring framework (integration)
    * Servlet Dispatcher
    * Bean injection
    
 3) Google Web Toolkit

to implement a demo of the WebXtraction library, that allows one
to extract normalized web items (links or images) from any url,
and have the robot also automatically follow subsequent navigation
links.
    
A demo is deployed to http://webxtractor.appspot.com/