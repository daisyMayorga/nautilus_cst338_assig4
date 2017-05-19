/* ---------------------------------------------------------------------------------------------------------------- 
Nautilus Group
Caleb Allen
Daisy Mayorga
David Harrison
Dustin Whittington
Michael Cline
CST 338
M4: Optical Barcode Reader Java Program
23 May 2017

PURPOSE:

----------------------------------------------------------------------------------------------------------------- */

public class Assig4
{

   public static void main(String[] args) 
   {
      
   }
}

interface BarcodeIO
{
   public boolean scan(BarcodeImage bc);
   public boolean readText(String text);
   public boolean generateImageFromText();
   public boolean translateImageToText();
   public void displayTextToConsole();
   public void displayImageToConsole();
}

class BarcodeImage implements Cloneable
{
   public static final int MAX_HEIGHT = 30;
   public static final int MAX_WIDTH = 65;
   private boolean[][] image_data;

   // Default Constructor to set all values of image_data to false;
   public BarcodeImage()
   {
      image_data = new boolean[MAX_HEIGHT][MAX_WIDTH];
      for (int row = 0; row < image_data.length; row++)
      {
         for (int column = 0; column < image_data[row].length; column++)
         {
            image_data[row][column] = false;
         }
      }
   }

   public BarcodeImage(String[] str_data)
   {
      image_data = new boolean[MAX_HEIGHT][MAX_WIDTH];
      // Make sure string is not null or larger than MAX_HEIGHT and MAX_WIDTH
      if (checkSize(str_data))
      {
         // Nested for loop to set str_data '*' values to true and ' ' to false
         // in image_data
         for (int row = 0; row < str_data.length; row++)
         {
            for (int width = 0; width < str_data[row].length(); width++)
            {
               if (str_data[row].charAt(width) == '*')
               {
                  image_data[MAX_HEIGHT - str_data.length + row][width] = true;
               } else if (str_data[row].charAt(width) == ' ')
               {
                  image_data[MAX_HEIGHT - str_data.length + row][width] = false;
               }
            }
         }
      }
      displayToConsole();
   }

   // Returns false if String array is null, or exceeds MAX_HEIGHT or MAX_WIDTH
   // Returns true if String array is smaller or same size as MAX_HEIGHT and
   // MAX_WIDTH
   private boolean checkSize(String[] data)
   {
      if (data == null)
      {
         return false;
      }
      if (data.length > MAX_HEIGHT)
      {
         return false;
      }
      for (String s : data)
      {
         if (s.length() > MAX_WIDTH)
         {
            return false;
         }
      }
      return true;
   }

   // returns a single pixel if row and col are valid values for image_data,
   // false if they are invalid values
   public boolean getPixel(int row, int col)
   {
      if (row > MAX_HEIGHT || row < 0)
      {
         return false;
      }
      if (col > MAX_WIDTH || col < 0)
      {
         return false;
      }
      return image_data[row][col];
   }

   // sets the pixel of image_data if row and col exist in image_data, returns
   // false otherwise
   public boolean setPixel(int row, int col, boolean value)
   {
      if (row > MAX_HEIGHT || row < 0)
      {
         return false;
      }
      if (col > MAX_WIDTH || col < 0)
      {
         return false;
      }
      image_data[row][col] = value;
      return true;
   }

   // Displays image_data to console
   private void displayToConsole()
   {
      for (int row = 0; row < image_data.length; row++)
      {
         for (int column = 0; column < image_data[row].length; column++)
         {
            System.out.print(image_data[row][column]);
         }
         System.out.println();
      }
   }

   // Returns a BarcodeImage object identical to current BarcodeImage object
   public BarcodeImage clone()
   {
      BarcodeImage clone = new BarcodeImage();
      for (int row = 0; row < MAX_HEIGHT; row++)
      {
         for (int width = 0; width < MAX_WIDTH; width++)
         {
            clone.setPixel(row, width, image_data[row][width]);
         }
      }
      return clone;
   }
}

class DataMatrix implements BarcodeIO
{
   public static final char BLACK_CHAR = '*';
   public static final char WHITE_CHAR = ' ';
   
   /*
    a single internal copy of any image scanned-in OR
   passed-into the constructor OR created by
   BarcodeIO's generateImageFromText().
    */
   private BarcodeImage image;
   
   /*
   a single internal copy of any text read-in OR
   passed-into the constructor OR created by
   BarcodeIO's translateImageToText(). 
    */
   private String text;
   
   
   /*
   two ints that are typically less than
   BarcodeImage.MAX_WIDTH and BarcodeImage.MAX_HEIGHT
   which represent the actual portion of the
   BarcodeImage that has the real signal.  This is
   dependent on the data in the image, and can change
   as the image changes through mutators.  It can be
   computed from the "spine" of the image.
    */
   private int actualWidth, actualHeight;
   
   
   /*
   Constructors.  Three minimum, but you could have more:
    */
   
   public DataMatrix()
   {
      /*
      constructs an empty, but non-null, image and text
      value.  The initial image should be all white,
      however, actualWidth and actualHeight should start
      at 0, so it won't really matter what's in this
      default image, in practice.  The text can be set
      to blank, "", or something like "undefined".
       */
   }
   
   public DataMatrix(BarcodeImage image)
   {
      /*
       sets the image but leaves the text at its default
      value.  Call scan() and avoid duplication of code
      here.
       */
   }
   
   public DataMatrix(String text)
   {
      /*
      sets the text but leaves the image at its default
      value. Call readText() and avoid duplication of
      code here.
       */
   }
   
   //Accessors for actualWidth and actualHeight but no mutators! (why?)
   public int getActualWidth()
   {
      return -1;
   }
   
   public int getActualHeight()
   {
      return -1;
   }
   
   public boolean scan( BarcodeImage bc )
   {
      /*
      accepts some image, represented as a BarcodeImage
      object to be described below, and stores a copy of
      this image. Depending on the sophistication of the
      implementing class, the internally stored image
      might be an exact clone of the parameter, or a
      refined, cleaned and processed image. Technically,
      there is no requirement that an implementing class
      use a BarcodeImage object internally, although we
      will do so. For the basic DataMatrix option, it
      will be an exact clone. Also, no translation is
      done here - i.e., any text string that might be
      part of an implementing class is not touched,
      updated or defined during the scan.
      
      FROM LATER IN THE SPEC: 
      a mutator for image.  Like the constructor;  in
      fact it is called by the constructor.  Besides
      calling the clone() method of the BarcodeImage
      class, this method will do a couple of things
      including calling cleanImage() and then set the
      actualWidth and actualHeight.  Because scan()
      calls clone(), it should deal with the
      CloneNotSupportedException by embeddingthe clone()
      call within a try/catch block.  Don't attempt to
      hand-off the exception using a "throws" clause
      in the function header since that will not be
      compatible with the underlying BarcodeIO
      interface.  The catches(...) clause can have an
      empty body that does nothing.
       */
      return false;
   }

   public boolean readText( String text )
   {
      /*
       accepts a text string to be eventually encoded in
      an image. No translation is done here - i.e., any
      BarcodeImage that might be part of an implementing
      class is not touched, updated or defined during
      the reading of the text.
      
      FROM LATER IN THE SPEC: 
      a mutator for text.  Like the constructor;  in
      fact it is called by the constructor.
       */
      return false;
   }

   public boolean generateImageFromText()
   {
      /*
      Not technically an I/O operation, this method
      looks at the internal text stored in the
      implementing class and produces a companion
      BarcodeImage, internally (or an image in whatever
      format the implementing class uses).  After this
      is called, we expect the implementing object to
      contain a fully-defined image and text that are in
      agreement with each other.   
      
      FROM 'OTHER CONSIDERATIONS':
      The methods generateImageFromText() and
      translateImageToText(), are the tricky parts, and
      it will help if you have some methods like the
      following to break up the work:  private char
      readCharFromCol(int col) and private boolean
      WriteCharToCol(int col, int code).  While you
      don't have to use these exact methods, you must
      not turn in huge methods generateImageFromText()
      and translateImageToText() that are not broken
      down to smaller ones.
       */
      return false;
   }

   public boolean translateImageToText()
   {
      /*
      Not technically an I/O operation, this method
      looks at the internal image stored in the
      implementing class, and produces a companion text
      string, internally.  After this is called, we
      expect the implementing object to contain a fully
      defined image and text that are in agreement with
      each other.
      
      FROM 'OTHER CONSIDERATIONS':
      The methods generateImageFromText() and
      translateImageToText(), are the tricky parts, and
      it will help if you have some methods like the
      following to break up the work:  private char
      readCharFromCol(int col) and private boolean
      WriteCharToCol(int col, int code).  While you
      don't have to use these exact methods, you must
      not turn in huge methods generateImageFromText()
      and translateImageToText() that are not broken
      down to smaller ones.
       */
      return false;
   }
   
   private char readCharFromCol(int col)
   {
      /*
      FROM 'OTHER CONSIDERATIONS':
      The methods generateImageFromText() and
      translateImageToText(), are the tricky parts, and
      it will help if you have some methods like the
      following to break up the work:  private char
      readCharFromCol(int col) and private boolean
      WriteCharToCol(int col, int code).  While you
      don't have to use these exact methods, you must
      not turn in huge methods generateImageFromText()
      and translateImageToText() that are not broken
      down to smaller ones.
       */
      return '#';
   }
   
   private boolean writeCharToCol(int col, int code)
   {
      /*
      FROM 'OTHER CONSIDERATIONS':
      The methods generateImageFromText() and
      translateImageToText(), are the tricky parts, and
      it will help if you have some methods like the
      following to break up the work:  private char
      readCharFromCol(int col) and private boolean
      WriteCharToCol(int col, int code).  While you
      don't have to use these exact methods, you must
      not turn in huge methods generateImageFromText()
      and translateImageToText() that are not broken
      down to smaller ones.
       */
      
      return false;
   }

   public void displayTextToConsole()
   {
      /*
      prints out text string to console
       */
   }

   public void displayImageToConsole()
   {
      /*
      prints out the image to the console.  In our
      implementation, we will do this in the form of a
      dot-matrix of blanks and asterisks, e.g.,
      
      UNDER 'OTHER CONSIDERATIONS':
       should display only the relevant portion of the
      image, clipping the excess blank/white from the
      top and right.  Also, show a border
      
      Caleb: SEE 'OTHER CONSIDERATIONS' FOR EXAMPLES
       */
   }
   
   private int computeSignalWidth()
   {
      /*
      Assuming that the image is correctly situated in
      the lower-left corner of the larger boolean array,
      these methods use the "spine" of the array (left
      and bottom BLACK) to determine the actual size.
       */
      return -1;
   }
   
   private int computeSignalHeight()
   {
      /*
      Assuming that the image is correctly situated in
      the lower-left corner of the larger boolean array,
      these methods use the "spine" of the array (left
      and bottom BLACK) to determine the actual size.
       */
      return -1;
   }
   
   private void cleanImage()
   {
      /*
      This private method will make no assumption about
      the placement of the "signal" within a passed-in
      BarcodeImage.  In other words, the in-coming
      BarcodeImage may not be lower-left justified. 
      
      Caleb: FULL EXPLANATION WITH EXAMPLES IN SPEC UNDER
      PHASE 3. TOO COMPLICATED TO PUT HERE.
       */
   }
   
   public void displayRawImage()
   {
      /*
      Optional - public void displayRawImage() can be
      implemented to show the full image data including
      the blank top and right.  It is a useful debugging
      tool.
       */
   }
   
   private void clearImage()
   {
      /*
      Optional - private void clearImage() - a nice
      utility that sets the image to white =  false.
       */
   }
}
   