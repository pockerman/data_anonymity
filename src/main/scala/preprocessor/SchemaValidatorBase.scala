package preprocessor

/**
 * class SchemaValidator. Given a schema and a dataset validates
 * the dataset accoring to the given schema
 */
abstract class SchemaValidatorBase {

  /**
   * Run the validator
   */
  def run(): Unit

}
