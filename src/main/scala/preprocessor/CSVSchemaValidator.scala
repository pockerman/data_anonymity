package preprocessor

import java.io.File
import org.deidentifier.arx.Data


/**
 * class CSVSchemaValidator. Validates a csv based dataset
 */
class CSVSchemaValidator(val schemaFile: File, val csvData: Data) extends SchemaValidatorBase {


  override def run(): Unit = {}
}
