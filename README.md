# Quick Scripting - iOS Location Extractor

In iOS Photos.sqlite, there is a table ZADDITIONALASSETATTRIBUTES with field "ZREVERSELOCATIONDATA". In
https://github.com/ScottKjr3347/iOS_Local_PL_Photos.sqlite_Queries it is represented as
"zAddAssetAttr-Reverse Location Data/Orig-Asset/HEX NSKeyed Plist" in the Basic Join.

This is actually an embedded bplist file. We can convert this to a readable dictionary using either the Notepad++ bplist plugin,
or more efficiently using a Java library.

The process is:

1) Dump your Photos.sqlite file somewhere on disk
2) Optionally: Use DBeaver to export a filtered list of the Basic SQL with null columns removed to a separate SQLITE file
3) Build the Java project (basic gradle build): ./gradlew uberJar
4) Run the build/libs/JavaBplistConv-1.0-SNAPSHOT.jar file: `java -jar JavaBplistConv-1.0-SNAPSHOT.jar <dbFile> <outFile>`
5) Trim the last \r\n or \n from the resulting text file manually

This extracts the data.

To import the data, in DBeaver:

1) Import the Text file into a table (to be deleted later). Make sure you set the field delimiter to something like § which is reasonably sure
not to be an actual location
2) run the following query:

```sql
select z.rowid,z.*,l.LOCATION  from ZASSET z
inner join Location l on l.rowid = z.rowid
```

Export that result into a new table.

