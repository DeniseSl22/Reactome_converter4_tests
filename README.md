# File names clean up

Currently the converter adds the full title of the PW from Reactome as the file name. This is inconvenient, since the further operations in this repository require no spaces and no brackets in the file names of GPMLs. A new version of the Reactome2GPML converter should fix this [issue](https://github.com/wikipathways/reactome2gpml-converter/issues/55).

Current solution:
1. In linux command line, use the following statement to remove all items brackets "()" in the file titles:
```
ls -d -- *\(*\)* | sed 's/\(.*\) (.*)\(.*\)/mv -- "&" "\1\2"/'
for file in ./*; do mv "$file" "${file/ (*)/}"; done
```
2. (again) In linux command line, use the following statement to replace all spaces " " in the file titles with an underscore "_":
```
for x in *" "*; do mv -- "$x" "${x// /_}" ; done
```
Check if all files are renamed accordingly (search for files with "(" or ")" ; (when one of these characters is connected to a letter, they're not replaced properly). Version 73 of Reactome had 3 cases where this happens; which were removed manually.

# GPML files

Save all PWs in the `GPMLs` folder from this repository.

# Generating Turtle

Generate the Turtle files (and unit test report) with the commands below.(GNU/Linux):

```shell
mkdir -p /tmp/OPSBRIDGEDB
echo "bridgefiles=/PATH/TO/BRIDGEDB/MAPPINGFILES/file" > /tmp/OPSBRIDGEDB/config.properties
make
```
make : creates the ttl file for each GPML (in the folder GPMLs)

make check : creates ttl files for each GPML (from the folder GPMLs) (ttls stored in /wp/Human) ; and a unit test report to check the content of the ttl (in /report).
