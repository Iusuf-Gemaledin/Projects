//This program sequences DNA series of nucleotides

public class DNA {
    public static void main(String[] args) {
                
      //  -. .-.   .-. .-.   .
      //    \   \ /   \   \ / 
      //   / \   \   / \   \  
      //  ~   `-~ `-`   `-~ `-
      String dna1 = "ATGCGATACGCTTGA";
      String dna2 = "ATGCGATACGTGA";
      String dna3 = "ATTAATATGTACTGA";
  
      String dna = dna3;  //Here you can change the dna to any of the three dna sequences
  
      //System.out.println(dna.length());
      //System.out.println(dna.indexOf("ATG"));
      //System.out.println(dna.indexOf("TGA"));
  
      if (dna.indexOf("ATG") != -1 && dna.indexOf("TGA") != -1 && (dna.indexOf("TGA") - dna.indexOf("ATG")) % 3 == 0) {
        String protein = dna.substring(dna.indexOf("ATG"), dna.indexOf("TGA") + 3);
        System.out.println("The protein is: " + protein);
      } else {
        System.out.println("There is no protein here :(");
      }
    }
  }