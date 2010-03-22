TRG=bandwidth-module

.PHONY: all clean doc release


copy: clean
	mkdir $(TRG)
	cp -r src/bandwidth/ $(TRG)
	cp *.jar dist/lib/*.jar config-bandwidth.txt README $(TRG)
	cp Makefilebm $(TRG)/Makefile
	rm -rf `find $(TRG) -iname ".svn"`

clean:
	rm -f `find -name "*.class"`
	rm -rf doc
	rm -f output.log
	rm -f bandwidth-aware.jar
	rm -rf $(TRG)

pack: clean copy
	tar -czvvf bandwidth-aware.tar.gz $(TRG)
