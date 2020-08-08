TARGETS := ${shell find . -name "*.gpml" }
TTLS    := ${shell find . -name "*.gpml" | sed -e 's/\.\/GPMLs\///' | sed -e 's/\(.*\).gpml/wp\/Human\/\1.ttl/' | sed -e 's/\s/\\ /g' }
REPORTS := ${shell find . -name "*.gpml" | sed -e 's/\.\/GPMLs\///' | sed -e 's/\(.*\)/reports\/\1.md/' | sed -e 's/\s/\\ /g' }

all: ${TTLS}

wp/Human/%.ttl: GPMLs/%.gpml src/java/main/org/wikipathways/covid/CreateRDF.class
	@mkdir -p wp/Human
	java -cp src/java/main/.:libs/GPML2RDF-3.0.0-SNAPSHOT-jar-with-dependencies.jar:libs/derby-10.5.3.0_1.jar org.wikipathways.covid.CreateRDF $< 1 | grep -v ".bridge" > $@

src/java/main/org/wikipathways/covid/CreateRDF.class: src/java/main/org/wikipathways/covid/CreateRDF.java
	@javac -cp libs/GPML2RDF-3.0.0-SNAPSHOT-jar-with-dependencies.jar src/java/main/org/wikipathways/covid/CreateRDF.java

src/java/main/org/wikipathways/covid/CheckRDF.class: src/java/main/org/wikipathways/covid/CheckRDF.java libs/wikipathways.curator-1-SNAPSHOT-jar-with-dependencies.jar
	@javac -cp libs/wikipathways.curator-1-SNAPSHOT-jar-with-dependencies.jar src/java/main/org/wikipathways/covid/CheckRDF.java

check: ${REPORTS}

reports/%.md: wp/Human/%.ttl src/java/main/org/wikipathways/covid/CheckRDF.class
	@mkdir -p reports
	@java -cp libs/jena-arq-3.16.0.jar:src/java/main/:libs/wikipathways.curator-1-SNAPSHOT-jar-with-dependencies.jar org.wikipathways.covid.CheckRDF $< > $@
