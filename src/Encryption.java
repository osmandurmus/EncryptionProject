import java.util.Random;

public class Encryption {

    // İki boyutlu matrise dönüştürülmüş matrisi tek bir dize olarak Diagonal okuma,
    // Oluşturalan tek boyutlu dizeyi 256'lık bloklara bölme,
    // Her bir blok için cipheredImage oluşturma
    public static int[][] run(int[][] imageMatrix,int round){

        if (round==0){
            return  imageMatrix;
        }

        int[] diagonalMatrix=DiagonalOrder.diagonalOrder(imageMatrix); //görüntü matrisinin diagonal şekilde tek boyutlu dizesi.

        int[][] partsOfImageMatrix=partBypart(diagonalMatrix); //her bir satırında 256 boyutunda blokları tutan matris.

        int[] initialVektor=createMatrixRandom(); //Initial vektörü, random bir şekilde oluşturur.

        int blockNumber=partsOfImageMatrix.length; //256'lık partlara ayırılan blok sayısını tutar.
        Object[] outputBlocks=new Object[blockNumber]; //Şifrelenen her bir bloğun depolanacağı yer.
        for(int index=0;index<blockNumber;index++) { // Tüm bloklar işlemlere sırayla dahil edilir.
            int[] cipheredImage=new int[256];

            for (int i=0;i<round;i++) { //Blok için aynı işlemler belirlenen Round sayısına göre tekrar eder.
                cipheredImage = exor(initialVektor, partsOfImageMatrix[index]); //Blok ile İnitilal vektörün exorlanması

                //int[] key = createMatrixRandom(); //Anahtarın oluşturulması

                cipheredImage = encryptionFunction(cipheredImage); //Encryption Fonksiyon ile yeni cipheredImage elde edilir.
            }
            initialVektor=cipheredImage; //Oluşturulan şifreli blok bir sonrraki bloğun initial vektörü olur.

            outputBlocks[index]=cipheredImage; //Şifreli blokların her bir çıktısı sırayla depolanır.
        }
        return getEncryptedImageMatrix(outputBlocks,imageMatrix.length,imageMatrix[0].length); //Şifreli iki boyutlu görüntü dizesi döndürülür.
    }

    //DES ve AES bileşenlerini kullanarak yeni bir cipheredImage (şifreli mesaj) döndürür.
    static int[] encryptionFunction(int[] cipheredImage){
        //DES bileşenleri ile yapılan işlemler
        int[][][] transposeMatrix = PerTabOperation.createTransposedSboxMatrixs(); //Des s-box tabloları tranpoze edilir.
        int[][] perTAB= PerTabOperation.createPerTabMatrix(transposeMatrix); //Permütasayon tablosu elde edilir.

        int[][] aMatrix= PerTabOperation.selectColumn(perTAB); //PerTab tablosundan rastgele seçilen sütun a
        int[][] bMatrix= PerTabOperation.selectColumn(perTAB); //PerTab tablosundan rastgele seçilen sütun b

        //AES bileşenleri ile yapılan işlemler
        ShiftOperation aes=new ShiftOperation(aMatrix,bMatrix); //DES' den gelen a ve b sütunları aes tablosu işlemlerine gönderilir.

        int[] resultArray=aes.shiftRows(); //AES  tablosunun satırlarını shift etme
        cipheredImage=exor(resultArray,cipheredImage); //aes dizesi ile cipheredImage exorlanır
        int[] key = createMatrixRandom(); //Anahtarın oluşturulması
        cipheredImage=exor(key,cipheredImage);//cipheredImage ile Anahtar exorlanır.

        resultArray=aes.shiftColumns(); //AES tablosunun sütunları shift etme
        cipheredImage=exor(resultArray,cipheredImage); //aes dizesi ile cipheredImage exorlanır
        key = createMatrixRandom(); //Anahtarın oluşturulması
        cipheredImage=exor(key,cipheredImage);//cipheredImage ile Anahtar exorlanır.

        return cipheredImage;

    }

    //Tek boyutlu görüntü dizesini 256'lık partlara bölen fonksiyon.
    static int[][] partBypart(int[] diagonalMatrix){
        int[][] blocksMatrix; // her bir satırında 256 uzunluğunda blokları tutan matrix.
        int part=diagonalMatrix.length/256;
        int remain=diagonalMatrix.length%256;

        Random r=new Random(); //random sınıfı ile kalan indexler doldurulacak rastgele şekilde

        if(remain!=0){
            part=part+1;
        }
        blocksMatrix=new int[part][256];
        int index=0;
        int lastPart=part-1;
        for(int i=0;i<part;i++){

            // Son part'ın 256 değere tamamlanması gerektiği durumda uygulanır.
            if((i==lastPart)&&remain>0){
                for(int k=remain;k<256;k++){
                    int rndNumber=r.nextInt(256);
                    blocksMatrix[i][k]=rndNumber;
                }
            }
            else {
                for(int j=0;j<256;j++){
                    blocksMatrix[i][j]=diagonalMatrix[index++];
                }
            }
        }
        return blocksMatrix;
    }

    //1x256 boyutunda Initial Vektör veye KEY dizisini oluşturulması
    static int[] createMatrixRandom(){
        Random r=new Random(); //random sınıfı
        int[] iv=new int[256];
        for(int i=0;i<256;i++){
            int rndNumber=r.nextInt(256);
            iv[i]=rndNumber;
        }
        return iv;
    }

    //EXOR operasyonu (tek boyutlu dize-EXOR-tek boyutlu dize)
    static int[] exor(int[] array1,int[] array2){
        int[] exoredArray=new int[256];
        try {
            for(int i=0;i<array1.length;i++){
                exoredArray[i]=array1[i]^array2[i];
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return exoredArray;
    }

    //cipheredImage bloklarının tek dize olarak birleştir
    public static int[] sifreliBloklarıBirlestir(Object[] outputBlocks){

        int[] array=new int[outputBlocks.length*((int[])outputBlocks[0]).length];
        int index=0;
        for(int i=0;i<outputBlocks.length;i++){
            int[] dizi=(int[])outputBlocks[i];
            for(int j=0;j<dizi.length;j++){
                array[index++]=dizi[j];
            }
        }
        return array;
    }

    //tek boyutlu diziyi iki boyutlu dizeye yazma
    public static int[][] convertTo2dArray(int[] array,int row,int col){
        int[][] sifreliMatris=new int[row][col];
        int index=0;
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                sifreliMatris[i][j]=array[index++];
            }
        }
        return sifreliMatris;
    }

    //Şifrelenen 256'lık blokları kullanarak, orjinal görüntü boyutunda  şifreli matris döndürür.
    public static int[][] getEncryptedImageMatrix(Object[] outputBlocks,int row,int col){
        int[] array=sifreliBloklarıBirlestir(outputBlocks);
        return convertTo2dArray(array,row,col);
    }
}
