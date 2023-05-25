import java.awt.Color;
import java.util.*;
import java.awt.Point;

/**
 * AP CS FINAL PROJECT: STEGANOGRAPHY
 * Welcome to the Steganography program! Put simply, by changing information 
 * in each pixel, both images and texts can be hidden and subsequently
 * revealed in images. Once you run this class, you will have several options
 * of exploring this program to choose from. Enjoy!
 *
 * @author Zoe Jovanovic
 * @version 24 May 2023
 */
public class Steganography
{
    /**
     * Clear the lower (rightmost) two bits in a pixel.
     */
    public static void clearLow(Pixel p)
    {
        p.setRed(p.getRed()/4*4);
        p.setGreen(p.getGreen()/4*4);
        p.setBlue(p.getBlue()/4*4);
    }
    
    /**
     * Set the lower 2 bits in a pixel to the highest 2 bits in c
     */
    public static void setLow(Pixel p, Color c)
    {
        clearLow(p);
        p.setRed(p.getRed() + c.getRed()/64);
        p.setGreen(p.getGreen() + c.getGreen()/64);
        p.setBlue(p.getBlue() + c.getBlue()/64);
    }
    
    /**
     * Sets the highest two bits of each pixel's colors
     * to the lowest two bits of each pixel's colors
     */
    public static Picture revealPicture(Picture hidden)
    {
        Picture copy = new Picture(hidden);
        Pixel[][] pixels = copy.getPixels2D();
        Pixel[][] source = hidden.getPixels2D();
        
        for(int r = 0; r < pixels.length; r++)
        {
            for(int c = 0; c < pixels[0].length; c++)
            {
                Color col = source[r][c].getColor();
                //(isolating lower bits and moving them
                pixels[r][c].setRed((col.getRed() % 4) * 64 + col.getRed() % 64);
                pixels[r][c].setGreen((col.getGreen() % 4) * 64 + col.getGreen() % 64);
                pixels[r][c].setBlue((col.getBlue() % 4) * 64 + col.getBlue() % 64); 
            }
        }
        
        return copy;
    }
    
    /**
     * tests the clearLow method.
     */
    public static Picture testClearLow(Picture pic)
    {
        Picture toModify = pic;
        for(int row = 0; row < toModify.getWidth(); row++)
        {
            for(int col = 0; col < toModify.getHeight(); col++)
                clearLow(toModify.getPixel(row, col));
        }
        return toModify;
    }
    
    /**
     * tests the setLow method.
     */
    public static Picture testSetLow(Picture pic, Color c)
    {
        Picture toModify = pic;
        for(int row = 0; row < toModify.getWidth(); row++)
        {
            for(int col = 0; col < toModify.getHeight(); col++)
                setLow(toModify.getPixel(row, col), c);
        }
        return toModify;
    }
    
    /**
     * Determines whether secret can be hidden in source, which is
     * true if source and secret are the same dimensions.
     * @param source is not null
     * @param secret is not null
     * @return true if secret can be hidden in source, false otherwise.
     */
    public static boolean canHide(Picture source, Picture secret)
    {
        if(secret.getWidth() <= source.getWidth() 
            && secret.getHeight() <= secret.getHeight())
            return true;
            
        return false;
    }
    
    /**
     * Creates a new Picture with data from secret hidden in data from source
     * @param source is not null
     * @param secret is not null
     * @return combined Picture with secret hidden in source
     * precondition: source is same width and height as secret
     */
    public static Picture hidePicture(Picture source, Picture secret, int startRow, int startColumn)
    {
        if(!canHide(source, secret))
            return null;
            
        Picture combined = source;
        Pixel[][] combinedPix = combined.getPixels2D();
        Pixel[][] secretPix = secret.getPixels2D();
        
        for(int r = startRow; r < startRow + secretPix.length; r++)
        {
            for(int c = startColumn; c < startColumn + secretPix[0].length; c++)
                setLow(combinedPix[r][c], secretPix[r - startRow][c - startColumn].getColor());
        }
        
        return combined;
    }
    
    /**
     * Checks whether two images are the same
     */
    public static boolean isSame(Picture p1, Picture p2)
    {
        Pixel[][] pixels1 = p1.getPixels2D();
        Pixel[][] pixels2 = p2.getPixels2D();
        if(p1.getWidth() != p2.getWidth() && p1.getHeight() != p2.getHeight())
            return false;
            
        for(int r = 0; r < pixels1.length; r++)
        {
            for(int c = 0; c < pixels1[0].length; c++)
            {
                if(!pixels1[r][c].getColor().equals(pixels2[r][c].getColor()))
                    return false;
            }
        }
        
        return true;
    }
    
    /**
     * Find the differences between the two pictures.
     */
    public static ArrayList<Point> findDifferences(Picture p1, Picture p2)
    {
        ArrayList<Point> diff = new ArrayList<>();
        Pixel[][] pixels1 = p1.getPixels2D();
        Pixel[][] pixels2 = p2.getPixels2D();
        //System.out.println(pixels1.length);
        if(p1.getWidth() != p2.getWidth() && p1.getHeight() != p2.getHeight())
            return diff;
            
        for(int r = 0; r < pixels1.length; r++)
        {
            for(int c = 0; c < pixels1[0].length; c++)
            {
                if(!pixels1[r][c].getColor().equals(pixels2[r][c].getColor())){
                    //System.out.println(new Point(r,c));
                    diff.add(new Point(r,c));}
            }
        }
        
        return diff;
    }
    
    /**
     * Outlines the different area in Red.
     */
    public static Picture showDifferentArea(Picture pic, ArrayList<Point> diff)
    {
        if(diff.size() == 0)
            return pic;
            
        Picture rec = pic;
        Pixel[][] pixels = rec.getPixels2D();
        int firstR = (int)diff.get(0).getX();
        int lastR = (int)diff.get(diff.size() - 1).getX();
        int firstC = (int)diff.get(0).getY();
        int lastC = (int)diff.get(diff.size() - 1).getY();
        
        for(int r = firstR; r <= lastR; r++)
        {
            pixels[r][firstC].setColor(new Color(255,0,0));
            pixels[r][lastC].setColor(new Color(255,0,0));
        }
        
        for(int c = firstC; c <= lastC; c++)
        {
            pixels[firstR][c].setColor(new Color(255,0,0));
            pixels[lastR][c].setColor(new Color(255,0,0));
        }
        
        return rec;
    }
    
    /**
     * Takes a string consisting of letters and spaces and
     * encodes the string into an arraylist of integers.
     * The integers are 1-26 for A-Z, 27 for space, and 0 for end of
     * string. The arraylist of integers is returned.
     * @param s string consisting of letters and spaces
     * @return ArrayList containing integer encoding of uppercase
     *  version of s
     */
    public static ArrayList<Integer> encodeString(String s)
    {
        s = s.toUpperCase();
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        ArrayList<Integer> result = new ArrayList<Integer>();
        for(int i = 0; i < s.length(); i++)
        {
            if(s.substring(i,i+1).equals(" "))
                result.add(27);
            
            else
                result.add(alpha.indexOf(s.substring(i,i+1)) + 1);
        }
        result.add(0);
        return result;
    }
    
    /**
     * Returns the string represented by the codes arraylist.
     * 1-26 = A-Z, 27 = space
     * @param codes encoded string
     * @return decoded string
     */
    public static String decodeString(ArrayList<Integer> codes)
    {
        String result = "";
        String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        for(int i = 0; i < codes.size(); i++)
        {
            if(codes.get(i) == 27)
                result = result + " ";
            
            else
                result = result + alpha.substring(codes.get(i)-1,codes.get(i));
        }
        
        return result;
    }
    
    /**
     * Given a number from 0 to 63, creates and returns a 3-element
     * int array consisting of the integers representing the
     * pairs of bits in the number from right to left.
     * @param num number to be broken up
     * @return bit pairs in number
     */
    private static int[] getBitPairs(int num)
    {
        int[] bits = new int[3];
        int code = num;
        for(int i = 0; i < 3; i++)
        {
            bits[i] = code % 4;
            code = code / 4;
        }
        
        return bits;
    }
    
    /**
     * Hide a string (must be only capital letters and spaces) in a picture.
     * The string always starts in the upper left corner.
     * @param source picture to hide string in
     * @param s string to hide
     * @return picture with hidden string
     */
    public static void hideText(Picture source, String s)
    {
        Pixel[][] pixels = source.getPixels2D();
        ArrayList<Integer> encoded = encodeString(s);
        for(int i = 0; i < s.length(); i++)
        {
            int[] bits = getBitPairs(encoded.get(i));
            clearLow(pixels[0][i]);
            pixels[0][i].setRed(pixels[0][i].getRed() + bits[0]);
            pixels[0][i].setGreen(pixels[0][i].getGreen() + bits[2]);
            pixels[0][i].setBlue(pixels[0][i].getBlue() + bits[1]);
        }
        clearLow(pixels[0][s.length()]);
        /*for(int i = 0; i < 3 * s.length(); i+=3)
        {
            for(int j = i; j < i + 3; j++)
            {
                int[] bits = getBitPairs(encoded.get(i));
                clearLow(pixels[0][i]);
                pixels[0][i].setRed(pixels[0][i].getRed() + bits[j-i]);
                pixels[0][i].setBlue(pixels[0][i].getBlue() + bits[j-i]);
                pixels[0][i].setGreen(pixels[0][i].getGreen() + bits[j-i]);
            }
        }*/
        /*for(int i : encoded)
        {
            int[] bits = getBitPairs(i);
            
        }*/
    }
    
    /**
     * Returns a string hidden in the picture
     * @param source picture with hidden string
     * @return revealed string
     */
    public static String revealText(Picture source)
    {
        String str;
        ArrayList<Integer> codes = new ArrayList<>();
        int i = 0;
        int num;
        Pixel[][] pixels = source.getPixels2D();
        while(pixels[0][i].getRed()%4 != 0 || pixels[0][i].getGreen()%4 != 0
            || pixels[0][i].getBlue()%4 != 0)
        {
            num = 1*(pixels[0][i].getRed()%4) + 4*(pixels[0][i].getBlue()%4) + 16*(pixels[0][i].getGreen()%4);
            codes.add(num);
            i++;
        }
        
        return decodeString(codes);
    }
    
    private static void simpleColor()
    {
        Picture beach = new Picture ("beach.jpg");
        beach.explore();
        Picture copy = revealPicture(testSetLow(beach, Color.PINK));
        copy.explore();
    }
    
    private static void hideSameSize()
    {
        Picture beach = new Picture ("beach.jpg");
        Picture blueMotorcycle = new Picture("blueMotorcycle.jpg");
        //blueMotorcycle.explore();
        Picture combined = hidePicture(beach, blueMotorcycle,0,0);
        combined.explore();
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you ready to see the hidden image? (y/n)");
        String str = scanner.nextLine();
        System.out.println("Either way, here it is");
        
        Picture revealed = revealPicture(combined);
        revealed.explore();
        
    }
    
    private static void hideDiffSize()
    {
        Picture beach = new Picture("beach.jpg");
        Picture robot = new Picture("robot.jpg");
        Picture flower1 = new Picture("flower1.jpg");
        //beach.explore();
        //robot.explore();
        
        Picture hidden1 = hidePicture(beach, robot, 65, 208);
        Picture hidden2 = hidePicture(hidden1, flower1, 280, 110);
        hidden2.explore();
        
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you ready to see the hidden image(s) in this image? (y/n)");
        String str = scanner.nextLine();
        System.out.println("Either way, here it is");
        
        Picture unhidden = revealPicture(hidden1);
        unhidden.explore();
    }
    
    private static void seeBorder()
    {
        Picture swan = new Picture("swan.jpg");
        Picture swan2 = new Picture("swan.jpg");
        hideText(swan, "SUMMER IS HERE");
        System.out.println("The amount of pixels that are different: " 
            + findDifferences(swan, swan2).size());
            
        swan = showDifferentArea(swan, findDifferences(swan,swan2));
        swan.explore();
        
        Picture island = new Picture("CumberlandIsland.jpg");
        Picture arch = new Picture("arch.jpg");
        Picture hidden = hidePicture(island, arch, 30, 30);
        hidden = showDifferentArea(hidden, findDifferences(island, hidden));
        hidden.explore();
        revealPicture(hidden).explore();
    }
    
    private static void textMessage()
    {
        Picture swan = new Picture("swan.jpg");
        swan.explore();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Are you ready to see the hidden message in this image? (y/n)");
        String str = scanner.nextLine();
        System.out.println("Either way, here it is");
        
        hideText(swan, "SUMMER IS HERE");
        System.out.println(revealText(swan));
    }
    
    public static void main(String[] args)
    {
        System.out.println("Welcome to the Steganography Program! Please choose");
        System.out.println("from the options below to explore Steganography:");
        System.out.println("1. Simple color encryption");
        System.out.println("2. Hiding a picture of the same size as the source");
        System.out.println("3. Hiding multiple picture of a different size than the source");
        System.out.println("4. Seeing a border of the hidden picture");
        System.out.println("5. Encrypting text into an image");
        System.out.print("Please enter your choice: ");
        Scanner scanner = new Scanner(System.in);
        int c = scanner.nextInt();
        
        if(c == 1)
            simpleColor();
        
        else if(c == 2)
            hideSameSize();
            
        else if(c == 3)
            hideDiffSize();
            
        else if(c == 4)
            seeBorder();
            
        else if(c == 5)
            textMessage();
        
        else
            System.out.println("Invalid input.");
        
        System.out.println();
    }
}
