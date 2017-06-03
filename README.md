# AgentApp

open as raw

1. use standalone-full.xml

2. add this to destinations
	<jms-topic name="topic/informTopic">
		<entry name="java:/jms/topic/informTopic"/>
	</jms-topic>
	<jms-topic name="topic/mojTopic">                       
		<entry name="java:jboss/exported/jms/topic/mojTopic"/>                  
	</jms-topic>

3. in <hornetq-server> add
	<security-enabled>false</security-enabled>

4. set offset for additional servers
