/**
 * Created by bhushan on 11/2/14.
 */
public class HelloWFPT {
    
    PrimitiveWFPT primitiveWFPT;

    public HelloWFPT() {
        this.primitiveWFPT = new PrimitiveWFPT();
        this.primitiveWFPT.set(10);
    }

    public HelloWFPT(PrimitiveWFPT helloWfpt) {
        this.primitiveWFPT = helloWfpt;
    }


    public static void main(String[] args) {
        HelloWFPT helloWFPT = new HelloWFPT();
        System.out.println("Hello people! My value is : " + helloWFPT.primitiveWFPT.get());
    }
}
