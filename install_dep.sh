mvn install:install-file \
  -DgroupId=org.chocosolver \
  -DartifactId=choco-solver \
  -Dpackaging=jar \
  -Dversion=4.10.7 \
  -Dfile=libs/choco-parsers-4.10.7-SNAPSHOT-jar-with-dependencies.jar \
  -DgeneratePom=true