TRG=bandwidth-module
BASE=$(HOME)/NetBeansProjects/
PSDIR=$(BASE)/peersim/source/
.PHONY: all clean doc release

clean:
	rm -f `find -name "*.class"`
	rm -rf doc
	rm -f output.log
	rm -f *.jar
	rm -rf $(TRG)

peersim:
	make -C $(PSDIR) release
	
jar: clean peersim
	cd src && javac -g -cp "$(PSDIR)/djep-1.0.0.jar:$(PSDIR)/jep-2.3.0.jar:$(PSDIR)/peersim-1.0.2.jar" `find bandwidth -iname "*.java"` && 	jar -cf ../bandwidth-aware.jar `find bandwidth/ -name "*.class"`

module: clean peersim jar
	mkdir $(TRG)
	cp bandwidth-aware.jar $(TRG)
	cp -r src/bandwidth $(TRG)
	cp $(PSDIR)/*.jar $(TRG)
	cp config-bandwidth.txt README $(TRG)
	cp Makefilebm $(TRG)/Makefile
	rm -rf `find $(TRG) -iname ".svn"`
	
pack: module
	tar -czvvf bandwidth-aware.tar.gz $(TRG)

all: pack