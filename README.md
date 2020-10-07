
# GPML files

No spaces, no brackets (in file names of GPMLs). Saved in the `GPMLs` folder.

# Generating Turtle

Generate the Turtle with these commands and manually copy/paste the content
into the `.ttl` files in this repository (GNU/Linux):

```shell
mkdir -p /tmp/OPSBRIDGEDB
echo "bridgefiles=/path/to/where/the/bridge/files/are" > /tmp/OPSBRIDGEDB/config.properties
make
```
make : creates the ttl file

make check : creates the report to check the content of the ttl (and possibly creates a new ttl).
