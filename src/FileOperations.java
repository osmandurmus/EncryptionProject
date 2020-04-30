import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class FileOperations {
    
    //Dosya okuma ve dosyayı matrise çevirme
    public static int[][] readFileConvertMatrix(String filePath) {

        try{
           File file = new File(filePath);
           BufferedImage img = ImageIO.read(file);
           int width = img.getWidth();
           int height = img.getHeight();
           int[][] matrixImage= new int[height][width];
           Raster raster = img.getData();
           for (int i = 0; i < height; i++) {
               for (int j = 0; j < width; j++) {
                   matrixImage[i][j] =raster.getSample(j, i, 0); // j=genişlik, i=yükseklik
               }
           }
           return matrixImage;
       }catch (Exception e){
           System.out.println("Exception readFileConvertMatrix");
           return null;
       }
    }

    //MxN' lik bir matrisden Gray-Scale bir görüntü dosyası oluşturur.
    public static BufferedImage convertMatrixtoImage(int[][] matrix){
        BufferedImage image;
        image=new BufferedImage(matrix[0].length,matrix.length,BufferedImage.TYPE_BYTE_GRAY); //bufferedImage parametreleri sırasıyla genişlik,yükseklik olarak ayarlanır.
        try {
            for(int i=0; i<matrix.length; i++) {
                for(int j=0; j<matrix[0].length; j++) {
                    int a = matrix[i][j];
                    Color newColor = new Color(a,a,a);
                    image.setRGB(j,i,newColor.getRGB()); // j=genişlik, i=yükseklik
                }
            }
            return image;
        }
        catch(Exception e) {
            System.out.println("Exception convertMatrixtoImage");
            return null;
        }
    }

    //Resimi image dosyası olarak locale kaydeder.
    public static boolean saveImageToFile(BufferedImage image,String filePath,String formatName){
        try {
            File output = new File(filePath);
            ImageIO.write(image, formatName, output);
            return true;
        }
        catch (IOException e){
            return  false;
        }
    }

}

