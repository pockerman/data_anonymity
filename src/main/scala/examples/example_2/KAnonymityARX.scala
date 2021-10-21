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



/**
 * Example1: Load data to ARX
 */
object KAnonymityARX
{

  def fakeData(): Data = {

    val dataFile: File = new File("/home/alex/qi3/data_anonymizer_scala/fake_data/fake_data.csv")
    val data: Data = Data.create(dataFile, Charset.defaultCharset, ',')

    // set the data DataType
    data.getDefinition.setDataType("name", DataType.STRING)
    data.getDefinition.setDataType("email", DataType.STRING)
    data.getDefinition.setDataType("ssns", DataType.STRING)
    data.getDefinition.setDataType("phone", DataType.STRING)
    data.getDefinition.setDataType("weight", DataType.DECIMAL)
    data.getDefinition.setDataType("height", DataType.DECIMAL)

    // set the type of of each attribute
    data.getDefinition.setAttributeType("name", AttributeType.IDENTIFYING_ATTRIBUTE)

    data.getDefinition.setAttributeType("ssns", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE)
    data.getDefinition.setAttributeType("email", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE)
    data.getDefinition.setAttributeType("weight", AttributeType.INSENSITIVE_ATTRIBUTE)
    data.getDefinition.setAttributeType("height", AttributeType.INSENSITIVE_ATTRIBUTE)
    data.getDefinition.setAttributeType("phone", AttributeType.INSENSITIVE_ATTRIBUTE)

    data
  }

  def simpleTable(): Data = {

    val dataFile: File = new File("/home/alex/qi3/data_anonymizer_scala/fake_data/Scenarios/Simple Table-Disk based simple table/data1/newData.txt")
    val data: Data = Data.create(dataFile, Charset.defaultCharset, ',')

    // set the data DataType
    data.getDefinition.setDataType("zipcode", DataType.INTEGER)
    data.getDefinition.setDataType("age", DataType.INTEGER)
    data.getDefinition.setDataType("creditcard", DataType.STRING)
    data.getDefinition.setDataType("gender", DataType.STRING)
    data.getDefinition.setDataType("salary", DataType.INTEGER)

    // set the type of of each attribute
    data.getDefinition.setAttributeType("creditcard", AttributeType.IDENTIFYING_ATTRIBUTE)

    data.getDefinition.setAttributeType("zipcode", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE)
    //data.getDefinition.setAttributeType("zipcode", AttributeType.INSENSITIVE_ATTRIBUTE)
    data.getDefinition.setAttributeType("age", AttributeType.SENSITIVE_ATTRIBUTE)
    data.getDefinition.setAttributeType("gender", AttributeType.SENSITIVE_ATTRIBUTE)

    data.getDefinition.setAttributeType("salary", AttributeType.SENSITIVE_ATTRIBUTE)
    data
  }

  def createData: Data = {

    // Define data// Define data

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



  //import org.deidentifier.arx.ARXLattice.ARXNode
  //import org.deidentifier.arx.ARXResult

  //import java.util

  def printResult(result: ARXResult, data: Data): Unit = { // Print time


    val df1 = new DecimalFormat("#####0.00")
    val sTotal = df1.format(result.getTime / 1000d) + "s"
    System.out.println(" - Time needed: " + sTotal)
    // Extract
    val optimum = result.getGlobalOptimum
    val dataDef = data.getDefinition
    val attrs: Set[String] = dataDef.getQuasiIdentifyingAttributes.asScala.toSet[String]

    val qis = attrs.toArray[String]

    if (optimum == null) {
      System.out.println(" - No solution found!")
      return
    }
    // Initialize
    val identifiers = new Array[StringBuffer](qis.size)
    val generalizations = new Array[StringBuffer](qis.size)
    var lengthI = 0
    var lengthG = 0
    for (i <- 0 until qis.size) {
      identifiers(i) = new StringBuffer
      generalizations(i) = new StringBuffer
      identifiers(i).append(qis(i))
      generalizations(i).append(optimum.getGeneralization(qis(i)))
      if (data.getDefinition.isHierarchyAvailable(qis(i))) generalizations(i).append("/").append(data.getDefinition.getHierarchy(qis(i))(0).length - 1)
      lengthI = Math.max(lengthI, identifiers(i).length)
      lengthG = Math.max(lengthG, generalizations(i).length)
    }
    // Padding
    for (i <- 0 until qis.size) {
      while ( {
        identifiers(i).length < lengthI
      }) identifiers(i).append(" ")
      while ( {
        generalizations(i).length < lengthG
      }) generalizations(i).insert(0, " ")
    }
    // Print
    System.out.println(" - Information loss: " + result.getGlobalOptimum.getLowestScore + " / " + result.getGlobalOptimum.getHighestScore)
    System.out.println(" - Optimal generalization")
    for (i <- 0 until qis.size) {
      System.out.println("   * " + identifiers(i) + ": " + generalizations(i))
    }
    System.out.println(" - Statistics")
    System.out.println(result.getOutput(result.getGlobalOptimum, false).getStatistics.getEquivalenceClassStatistics)
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


    // Only excerpts for readability// Only excerpts for readability
    val zipcode = Hierarchy.create
    zipcode.add("81667", "8166*", "816**", "81***", "8****", "*****")
    zipcode.add("81675", "8167*", "816**", "81***", "8****", "*****")
    zipcode.add("81925", "8192*", "819**", "81***", "8****", "*****")
    zipcode.add("81931", "8193*", "819**", "81***", "8****", "*****")

    data.getDefinition.setAttributeType("age", age)
    data.getDefinition.setAttributeType("gender", gender)
    data.getDefinition.setAttributeType("zipcode", zipcode)

    System.out.println("Number of sensitive variables=" + data.getHandle.getDefinition.getSensitiveAttributes.size)


    // Create an instance of the anonymizer// Create an instance of the anonymizer

    val anonymizer = new ARXAnonymizer
    val config = ARXConfiguration.create
    config.addPrivacyModel(new KAnonymity(3))
    config.setSuppressionLimit(0d)


    val result = anonymizer.anonymize(data, config)


    // Print info
    printResult(result, data)

    // Process results// Process results

    System.out.println(" - Transformed data:")
    val transformed = result.getOutput(false).iterator
    while ( {
      transformed.hasNext
    }) {
      System.out.print("   ")

      val item = transformed.next
      System.out.println(item.mkString(" "))
    }






  }
}