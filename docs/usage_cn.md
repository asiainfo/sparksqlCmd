Hive数据导出工具
----
本项目是通过sql的方式将hive中的数据导出到本地或者hdfs中

####配置文件
主要配置有running.properties, spark-yarn.properties和sql_definition.json
- running.properties

  | 名字        | 说明           |
  | :------------- |:-------------|
  | sql.type      | 指定执行sql的方式，只能是thrift和hiveContext。默认thrift导出结果到本地，hiveContext导出到hdfs |
  | jdbc.uri      | 如果sql.type配置的为thrift，必须配置      | 
  | zoutput.table.header.enable | 输出结果是否带表头      |
  |output.table.column.separator|输出结果列分隔符|
- spark-yarn.properties

   如果sql.type配置的为hiveContext，需要在这个里面配置spark运行需要的参数。
- sql_definition.json

  sql的定义文件，可以配置多个sql，每个sql需要配置输出路径，注意outputPath配置的是一个文件不是文件夹，即最终sql结果输出的文件
；alias只是一个注释，无实际用途。

####如何运行
执行bin下的export.sh脚本。如果sql.type配置的为thrift，需要制定sql定义的配置文件，例如
`bin/export.sh conf/sql_definition.json`

如果sql.type配置的为hiveContext，不但要指定sql的配置，还要指定spark的home目录，例如
`bin/export.sh conf/sql_definition.json /usr/hdp/2.4.0.0-169/spark`

