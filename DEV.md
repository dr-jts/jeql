# JEQL Development
==================

## Eclipse Setup

### Projects

* jeql - module with JEQL core
  * Libraries from lib dir
* jeqlw - module with JEQLW
  * Requires: jeql
  
### Run Configurations

* JEQL Unit Tests
  * Project; jeql
  * Class: `test.jeql.TestUnit`
  * VM Args: -Xmx1000M
  * Working directory: ${workspace_loc:jeql/script}
  
* JEQL Command
  * Project; jeql
  * Class: `jeql.JeqlCmd`
  * Program Arguments: <script name>
  * Working directory: as required ( e.g. <home>/script/unitTest )

* JEQL Workbench
  * Project; jeqlw
  * Class: `jeql.workbench.Workbench`
  * VM Args: -Xmx1000M
  * Working directory: as required ( e.g. <home>/script/unitTest )
