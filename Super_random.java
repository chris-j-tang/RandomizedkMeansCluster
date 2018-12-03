package general;

import java.util.Random;

public class Super_random 
{
	public int get_random_inclusize_int(int a, int b)
	{
		Random rand = new Random();
		int z = rand.nextInt(b-a + 1);
		z = z + a;
		return z;
	}
}
