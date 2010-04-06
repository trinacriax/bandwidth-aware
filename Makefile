TRG=bandwidth-module
BASE=$(HOME)/NetBeansProjects/

.PHONY: all clean doc release

all: pack

base:
	cd $(BASE)/peersim/source/ && make release && cp *.jar $(BASE)/bandwidth-aware/
	echo "Current dir is " `pwd`

copy: clean base
	mkdir $(TRG)
	cp -r src/bandwidth/ $(TRG)
	mv *.jar $(TRG)
	cp config-bandwidth.txt README $(TRG)
	javac -cp "$(TRG)/djep-1.0.0.jar:$(TRG)/jep-2.3.0.jar:$(TRG)/peersim-1.0.2.jar" `find $(TRG)/bandwidth -iname "*.java"`
	jar cf $(TRG)/bandwidth-aware.jar `find $(TRG)/bandwidth/ -name "*.class"`
	cp Makefilebm $(TRG)/Makefile
	rm -rf `find $(TRG) -iname ".svn"`	
	
clean:
	rm -f `find -name "*.class"`
	rm -rf doc
	rm -f output.log
	rm -f bandwidth-aware.jar
	rm -rf $(TRG)

pack: copy
	tar -czvvf bandwidth-aware.tar.gz $(TRG)
