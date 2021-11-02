package example_2

import org.deidentifier.arx.ARXPopulationModel.Region

import java.io.File
import java.nio.charset.Charset
import java.text.DecimalFormat
import collection.JavaConverters.*
import collection.mutable.ArrayBuffer

import org.deidentifier.arx.{ARXAnonymizer, ARXConfiguration, ARXPopulationModel, ARXResult, AttributeType, Data, DataHandle, DataType}
import org.deidentifier.arx.criteria.KAnonymity
import org.deidentifier.arx.criteria.EqualDistanceTCloseness
import org.deidentifier.arx.criteria.HierarchicalDistanceTCloseness
import org.deidentifier.arx.criteria.DistinctLDiversity
import org.deidentifier.arx.Data
import org.deidentifier.arx.Data.DefaultData
import org.deidentifier.arx.AttributeType.Hierarchy
import org.deidentifier.arx.AttributeType.Hierarchy.DefaultHierarchy

import postprocessor.ResultPrinter.{printResult, printHandle}



/**
 * Example1: Load data to ARX
 */
object KAnonymityARX
{

  def createData: Data = {

    // Define data
    val data = Data.create
    data.add("age", "gender", "zipcode")
    data.add("34", "male", "81667")
    data.add("45", "female", "81675")
    data.add("66", "male", "81925")
    data.add("70", "female", "81931")
    data.add("34", "female", "81931")
    data.add("70", "male", "81931")
    data.add("45", "male", "81931")
    data
  }

  def main(args: Array[String]): Unit ={

    System.out.println("Running example 2...")

    val data = createData

    // check the columns
    val nCols = data.getHandle.getNumColumns
    println(s"Number of columns ${nCols}")

    val nRows = data.getHandle.getNumRows
    println(s"Number of rows ${nRows}")

    // define hierarchies
    val age = Hierarchy.create
    age.add("34", "<50", "*")
    age.add("45", "<50", "*")
    age.add("66", ">=50", "*")
    age.add("70", ">=50", "*")

    val gender = Hierarchy.create
    gender.add("male", "*")
    gender.add("female", "*")

    // Only excerpts for readability
    val zipcode = Hierarchy.create
    zipcode.add("81667", "8166*", "816**", "81***", "8****", "*****")
    zipcode.add("81675", "8167*", "816**", "81***", "8****", "*****")
    zipcode.add("81925", "8192*", "819**", "81***", "8****", "*****")
    zipcode.add("81931", "8193*", "819**", "81***", "8****", "*****")

    data.getDefinition.setAttributeType("age", age)
    data.getDefinition.setAttributeType("gender", gender)
    data.getDefinition.setAttributeType("zipcode", zipcode)

    System.out.println("Number of sensitive variables=" + data.getHandle.getDefinition.getSensitiveAttributes.size)

    // Create an instance of the anonymizer
    val anonymizer = new ARXAnonymizer
    val config = ARXConfiguration.create
    config.addPrivacyModel(new KAnonymity(3))
    config.setSuppressionLimit(0d)
    val result = anonymizer.anonymize(data, config)

    // Print info
    printResult(result, data)

    // Process results
    System.out.println(" - Transformed data:")
    printHandle(handle = result.getOutput(false))
    System.out.println("Done!")

  }
}