.PHONY: all clean doc release

all:
	javac -classpath .:peersim.jar `find -name *.java`
	rm -f bandwidth-aware.jar
	jar cf bandwidth-aware.jar `find -name *.class`


clean:
	rm -f `find -name "*.class"`
	rm -rf doc
	rm -f output.log
	rm -f bandwidth-aware.jar

doc:
	rm -rf doc/*
	javadoc -classpath "peersim.jar" -d doc -subpackages bandwidth:banwidth.core:bandiwdth.test -group "Bandwidth" "bandwidth" \
	 -windowtitle "Bandwidth Aware Module Documentation" `find . -name "*.java"`

release: clean all
	rm -f bandwidth-aware.jar
	rm -rf `find -name *.class`

run: all
	java -cp "peersim.jar:djep-1.0.0.jar:jep-2.3.0.jar:bandwidth-aware.jar" peersim.Simulator config-bandwidth.txt > output.log
	less output.log