package example_1

import java.io.File
import java.nio.charset.Charset
import org.deidentifier.arx.Data
import org.deidentifier.arx.AttributeType

/**
 * Example1: Load data to ARX
 */
object LoadDataToARX
{

  def main(args: Array[String]): Unit ={

    System.out.println("Running example 1...")
    val dataFile: File = new File("./example_1_data.csv")
    val data: Data = Data.create(dataFile, Charset.defaultCharset, ',')

    // define the attribute types

    data.getDefinition.setAttributeType("age", AttributeType.IDENTIFYING_ATTRIBUTE)
    data.getDefinition.setAttributeType("gender", AttributeType.SENSITIVE_ATTRIBUTE)
    data.getDefinition.setAttributeType("zipcode", AttributeType.INSENSITIVE_ATTRIBUTE)

    System.out.println("Data toString: " + data.toString)
    System.out.println("Done...")

  }
}