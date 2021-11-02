package example_1

import java.io.File
import java.nio.charset.Charset
import org.deidentifier.arx.Data
import org.deidentifier.arx.AttributeType
import postprocessor.ResultPrinter.{printHandleTop}

import base.DefaultConfiguration

/**
 * Example1: Load data to ARX
 */
object LoadDataToARX
{

  def main(args: Array[String]): Unit ={

    System.out.println("Running example 1...")
    val dataFile: File = new File(DefaultConfiguration.getDefaultDataPath + "/mocksubjects.csv")
    val data: Data = Data.create(dataFile, Charset.defaultCharset, ',')

    // define the attribute types
    System.out.println(s"Number of rows ${data.getHandle.getNumRows}")
    System.out.println(s"Number of cols ${data.getHandle.getNumColumns}")

    printHandleTop(handle = data.getHandle, n = 5)
    System.out.println("Done...")

  }
}