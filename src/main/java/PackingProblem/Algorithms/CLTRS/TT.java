package PackingProblem.Algorithms.CLTRS;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import static java.lang.Math.pow;

public class TT {
    //q, r, s
    private final long[][] zobrist;
    private final int hashKeyBytes = 3;
    private final int hashKeyBits = hashKeyBytes*8;
    private TTElement[] tt;
    public int collisionCounter = 0;

    public TT(int max_depth, int num_blocks){
        // Max depth is probalby a guess in order to not make it too large
        //Create the random values for every parcel at every placement depth
        zobrist = new long[max_depth][num_blocks];

        //Fill them with random long values
        for(int q = 0; q < max_depth; q++){
            for(int r = 0; r < num_blocks; r++){
                zobrist[q][r] = ThreadLocalRandom.current().nextLong();
            }
        }

        //Create the transposition table with size 2^hashKeyBits
        this.tt = new TTElement[(int)pow(2,hashKeyBits)];
    }

    //fast way to convert long to bytes and back
    public static byte[] longToBytes(long l) {
        byte[] result = new byte[8];
        for (int i = 7; i >= 0; i--) {
            result[i] = (byte)(l & 0xFF);
            l >>= 8;
        }
        return result;
    }

    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < b.length; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result;
    }

    //Return the index if found, otherwise add it to the table and return -1
    public TTElement checkTT(long hash){
        //Most efficient way I could find to split long
        byte[] hashBytes = longToBytes(hash);

        //Long is 64 bits thus 8 bytes
        byte[] hashKeyByte = new byte[hashKeyBytes];
        byte[] primaryHashByte = new byte[8-hashKeyBytes];

        //Copy the hash into the two keys
        for(int i = 0; i < 8; i++){
            if(i < hashKeyBytes){
                hashKeyByte[i] = hashBytes[i];
            }else{
                primaryHashByte[i-hashKeyBytes] = hashBytes[i];
            }
        }

        //Convert them back to Long's
        long primaryHash = bytesToLong(primaryHashByte);
        int hashKey = (int)bytesToLong(hashKeyByte);

        //Check if it is present in the tt
        TTElement contents = tt[hashKey];
        if(contents == null){
            //Not found in tt, return null
            return null;
        }else{
            //this hashKey is not emtpy, check for collision
            if(contents.primaryHash == primaryHash){
                //Collision occurred
                return null;
            }
            else{
                //No collision, return the found element
                //TODO:this disables the TT
                //return null;

                return contents;
            }
        }
    }

    public void storeTT(long hash, double value){
        //Most efficient way I could find to split long
        byte[] hashBytes = longToBytes(hash);

        //Long is 64 bits thus 8 bytes
        byte[] hashKeyByte = new byte[hashKeyBytes];
        byte[] primaryHashByte = new byte[8-hashKeyBytes];

        //Copy the hash into the two keys
        for(int i = 0; i < 8; i++){
            if(i < 8-hashKeyBytes){
                primaryHashByte[i] = hashBytes[i];
            }else{
                hashKeyByte[i-(8-hashKeyBytes)] = hashBytes[i];
            }
        }

        //Convert them back to Long's
        long primaryHash = bytesToLong(primaryHashByte);
        int hashKey = (int)bytesToLong(hashKeyByte);

        //Make the tt element
        TTElement ttElem = new TTElement(primaryHash, value);

        //Check if it is present in the tt
        TTElement contents = tt[hashKey];
        if(contents == null){
            //Element is empty, add it
            tt[hashKey] = ttElem;
        }else{
            //this hashKey is not emtpy, check for collision
            if(contents.primaryHash == primaryHash){
                //Collision occurred
                //Replacement strategy is here new, therefore do not return this value
                collisionCounter += 1;
                tt[hashKey] = ttElem;

//                System.out.println("Collision occured in TT");
            }
            else{
                //No collision, this one is already taken, you should not be here
//                try {
//                    throw new Exception("Invalid state store tt");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

    //Calculates the hash of a state
    public long getHash(State state) {
        long hashKey = 0;

        ArrayList<Integer> placed_ids = state.getBlock_placements();

        for(int i = 0; i < placed_ids.size(); i++){
            hashKey ^= zobrist[i][placed_ids.get(i)];
        }

        return hashKey;
    }

    public long updateHash(long hashKey, int move_num, int placed_id) {
        hashKey ^= zobrist[move_num][placed_id];
        return hashKey;
    }


}