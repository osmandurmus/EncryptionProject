import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class MainGUI {

    static JFrame frame;
    static JPanel rootPanel;
    static JPanel topPanel;
    static JPanel centerPanel;
    static JPanel bottomPanel;
    static JLabel labelOrijinalImage;
    static JLabel labelEncryptedImage;
    static JButton buttonSelectYourPhoto;
    static JButton buttonEncryption;
    static JComboBox comboBoxRoundNumber;
    static JLabel labelRoundNumber;

    static BufferedImage bufferedEncryImage;
    static String filePath;
    static String fileName;
    static int comboboxRoundNumber;

    public static void main(String[] args) throws Exception
    {
        createViewComponents(); //komponentleri oluşturma

        buttonSelectYourPhoto.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectImage(); // Resim dosyası seçer
            }
        }); //Resim seçme

        buttonEncryption.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(filePath!=null) //Resim seçimi yapıldı ise
                    doEncryption();
                else
                    JOptionPane.showMessageDialog(frame, "Please choose a picture first!");
            }
        }); //Seçilen resmi şifreleyip gösterme

        //Frame kapatılınca programın sonlandırılması.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
    }
    //Komponentleri oluşturmak.
    public static void createViewComponents(){
        // create a new frame to store text field and button
        frame = new JFrame("Image Encryption");

        // create a label to display image
        labelOrijinalImage = new JLabel();
        labelEncryptedImage =new JLabel();


        //create a button
        buttonSelectYourPhoto =new JButton("Select Image");
        buttonEncryption =new JButton("Encrypt");

        //create combobox and fill in
        comboBoxRoundNumber=new JComboBox();
        comboBoxRoundNumber.addItem(8);
        comboBoxRoundNumber.addItem(12);
        comboBoxRoundNumber.addItem(16);

        //create label
        labelRoundNumber=new JLabel("Round Number:");

        // create a rootPanel
        rootPanel = new JPanel(new BorderLayout());
        topPanel = new JPanel();
        centerPanel = new JPanel(new FlowLayout());
        bottomPanel = new JPanel();

        topPanel.add(labelOrijinalImage);
        centerPanel.add(buttonSelectYourPhoto);
        centerPanel.add(labelRoundNumber);
        centerPanel.add(comboBoxRoundNumber);
        centerPanel.add(buttonEncryption);
        bottomPanel.add(labelEncryptedImage);

        rootPanel.add(topPanel,BorderLayout.NORTH);
        rootPanel.add(centerPanel,BorderLayout.CENTER);
        rootPanel.add(bottomPanel,BorderLayout.SOUTH);

        // add rootPanel to frame
        frame.add(rootPanel);


        // set the size of frame
        frame.setSize(720, 720);
        frame.setVisible(true);

    }

    //Resim dosyası seçme ve dosya uzantısını almak.
    public static void selectImage(){
        try {
            JFileChooser fChooser=new JFileChooser();

            //Filtreleme, sadece resim dosyası uzantılı dosyalar görünür.
            fChooser.addChoosableFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "bmp","jpeg"));
            fChooser.setAcceptAllFileFilterUsed(true);

            fChooser.showOpenDialog(frame);
            if(fChooser.getSelectedFile().getAbsolutePath()!=null){
                filePath=fChooser.getSelectedFile().getAbsolutePath(); //Resim dosyasının uzantısını alma.
                fileName=fChooser.getSelectedFile().getName(); //Resim dosyasının ismini alma

                loadImageOrijinal(); // Resim dosyası yükleme
            }
        }catch (Exception ex){
            System.out.println("Exception buttonSelectYourPhoto actionPerformed");
            ex.printStackTrace();
        }
    }

    //Seçili resimi göstermek için label komponentine yükler.
    public static  void loadImageOrijinal(){
        // create a new orijinal image icon
        ImageIcon loadedImage = new ImageIcon(filePath);
        loadedImage= setSizeImageIcon(loadedImage); //Scale for loaded Image
        labelOrijinalImage.setIcon(loadedImage);
    }

    //Şifrelemiş resimi göstermek için label komponentine yükler.
    public static  void loadImageEncrypted(BufferedImage image){
        // create a new encrypted image icon
        ImageIcon encryImage=new ImageIcon(bufferedEncryImage);
        encryImage= setSizeImageIcon(encryImage); //Scale for encry Image
        labelEncryptedImage.setIcon(encryImage);
    }

    //ImageIcon'un standart bir boyuta dönüştürülmesi
    public static ImageIcon setSizeImageIcon(ImageIcon scaledImageIcon){

        Image image = scaledImageIcon.getImage(); // transform it
        Image newimg = image.getScaledInstance(300, 300,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way
        scaledImageIcon = new ImageIcon(newimg);  // transform it back

        return scaledImageIcon;
    }

    public static void doEncryption(){
        try {
            comboboxRoundNumber= (int) comboBoxRoundNumber.getSelectedItem(); //(round) adım sayısını alma.

            int[][] imageMatrix=FileOperations.readFileConvertMatrix(filePath); //Resimi matrise çevirme.
            int[][] encryImageMatrix= Encryption.run(imageMatrix,comboboxRoundNumber); //Şifreli resim matrisini elde etme.

            //Şifrelenen matrisin, resim dosyası olarak kaydedilmesi.
            bufferedEncryImage=FileOperations.convertMatrixtoImage(encryImageMatrix); //Şifreli resim matrisini resime dönüştürme.
            loadImageEncrypted(bufferedEncryImage); // create a new encrypted image icon

            //Şifreli resimi jpg formatında local'e kaydetme.
            if(FileOperations.saveImageToFile(bufferedEncryImage,"encry-"+fileName,"jpg")){
                System.out.println("OK");
            }
            else
                System.out.println("NO");

        }catch (Exception e){
            System.out.println("Fault buttonEncryption action performed");
            e.printStackTrace();
        }
    }
}
