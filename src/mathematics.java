
public class mathematics {

	
	public float[] vectorCross(float a[],float b[]){
		float ret[]=new float[3];
		ret[0]=(a[1]*b[2])-(a[2]*b[1]);
		ret[1]=(a[2]*b[0])-(a[0]*b[2]);
		ret[2]=(a[0]*b[1])-(a[1]*b[0]);
		return ret;
	}
	
	public float[] vectorScale(float[] scale, float by){
		float[] ret=new float[3];
		ret[0]=scale[0];
		ret[1]=scale[1];
		ret[2]=scale[2];
		for(int i=0;i<ret.length;i++){
			ret[i]=ret[i]*by;
		}
		return ret;
	}
	
	public float[][] invertMatrix(float[][] inv){
		float[][] ret=new float[3][3];
		ret[0][0]=(inv[1][1]*inv[2][2])-(inv[1][2]*inv[2][1]);
		ret[0][1]=(inv[0][1]*inv[2][2])-(inv[0][2]*inv[2][1]);
		ret[0][2]=(inv[0][1]*inv[1][2])-(inv[0][2]*inv[1][1]);
		ret[1][0]=(inv[1][0]*inv[2][2])-(inv[1][2]*inv[2][0]);
		ret[1][1]=(inv[0][0]*inv[2][2])-(inv[0][2]*inv[2][0]);
		ret[1][2]=(inv[0][0]*inv[1][2])-(inv[0][2]*inv[1][0]);
		ret[2][0]=(inv[1][0]*inv[2][1])-(inv[1][1]*inv[2][0]);
		ret[2][1]=(inv[0][0]*inv[2][1])-(inv[0][1]*inv[2][0]);
		ret[2][2]=(inv[0][0]*inv[1][1])-(inv[0][1]*inv[1][0]);
		ret[1][0]=ret[1][0]*-1;
		ret[0][1]=ret[0][1]*-1;
		ret[2][1]=ret[2][1]*-1;
		ret[1][2]=ret[1][2]*-1;
		for(int i=0;i<3;i++){
			for(int j=0;j<3;j++){
				ret[i][j]=ret[i][j]/this.determinant(inv);
			}
		}
		return ret;
	}
	
	public float determinant(float[][] a){
		float ret;
		ret=a[0][0]*a[1][1]*a[2][2];
		ret+=a[1][0]*a[2][1]*a[0][2];
		ret+=a[2][0]*a[0][1]*a[1][2];
		ret=ret-(a[0][2]*a[1][1]*a[2][0]);
		ret=ret-(a[1][2]*a[2][1]*a[0][0]);
		ret=ret-(a[2][2]*a[0][1]*a[1][0]);
		return ret;
	}
	
	public float[]	vectorAdd(float[] a, float[] b){
		if(a.length!=b.length)return null;
		float[] ret=new float[3];
		for(int i=0;i<a.length;i++){
			ret[i]=a[i]+b[i];
		}
		return ret;
	}
	
	public float[]	vectorSubtract(float[] a, float[] b){
		if(a.length!=b.length)return null;
		float[] ret=new float[3];
		for(int i=0;i<a.length;i++){
			ret[i]=a[i]-b[i];
		}
		return ret;
	}
	

	
	public float[] normalize(float a[]){
		float div=(float)Math.sqrt((a[0]*a[0])+(a[1]*a[1])+(a[2]*a[2]));
		for(int i=0;i<3;i++){
			a[i]=a[i]/div;
		}
		return a;
	}
	
	public float distance(float a[], float b[]){
		return (float)Math.sqrt((a[0]-b[0])*(a[0]-b[0])+(a[1]-b[1])*(a[1]-b[1])+(a[2]-b[2])*(a[2]-b[2]));
	}
	
	public float[][] matrixIdent(float a[][]){
		if(a.length!=a[0].length){
			System.out.print("Can't make identity, not a square matrix");
			return a;
		}
		for(int i=0; i<a.length;i++){
			for(int j=0;j<a.length;j++){
				if(i==j)a[i][j]=1;
				else a[i][j]=0;
			}
		}
		return a;
	}
	
	public void printMatrix(float a[][]){
		for(int i=0;i<a[0].length;i++){
			for(int j=0; j<a.length; j++)
				System.out.print(a[j][i]+" ");
			System.out.println();
		}
		
	}
	
	public void printMatrix(int a[][]){
		for(int i=0;i<a[0].length;i++){
			for(int j=0; j<a.length; j++)
				System.out.print(a[j][i]+" ");
			System.out.println();
		}
		
	}
	
	public void printVector(float a[]){
		for(int i=0;i<a.length;i++){
			System.out.print(a[i]+" ");
		}
		System.out.println();
	}
	
	public float[][] matrixMultiply(float a[][], float b[][]){
		if(a.length!=b[0].length)return null;
		float[][] ret=new float[b.length][a[0].length];
		for(int i=0;i<b.length;i++){
			for(int j=0;j<a[0].length;j++){
				for(int k=0;k<a.length;k++){
					ret[i][j]+=(a[k][j]*b[i][k]);
				}
			}
		}
		return ret;
	}
	
	
	public static void main(String args[]){
		
		
	}
	
	
}
