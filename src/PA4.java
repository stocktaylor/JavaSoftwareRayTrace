import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PA4 {

	Model[] collection;
	float[][] Lights;
	float[][][] image;
	float[][][] image1;
	float[][][] image2;
	float[][][] image3;
	float[][][] image4;
	float[][][] image5;
	int[][][] imageint;
	float[] ambient;
	float[] temp;
	float[] temp2;
	float rfc, shine;
	int xmin;
	int xmax;
	int ymin;
	int ymax;
	int xoffset;
	int yoffset;
	float intersectedPoly[][][];
	Camera cam;
	
	public static void main(String[] args) {
		System.out.println("Running");
		if(args.length<3){
			System.out.println("Not enough arguments given! Exiting...");
			System.exit(1);
		}
		PA4 sh= new PA4();
		String[] subArgs=new String[args.length-3];
		for(int i=2;i<args.length-1;i++){
			subArgs[i-2]=args[i];
		}
		sh.readModels(subArgs);
		sh.readCamera(args[0]);
		sh.readMaterial(args[1]);
		for(int i=0;i<5;i++){
			sh.makeImage(i);
			System.out.println(20*(i+1)+"% done!");
		}
		sh.imageAvgAA();
		sh.normalizeImage();
		sh.imageInt();
		sh.createOutput(args[args.length-1]);
		System.out.println("Finished");
		

	}
	
	public void createOutput(String output){
		PrintWriter pw=null;
		try {
			pw = new PrintWriter(output);
		} catch (FileNotFoundException e) {
			System.out.println("Cannot create file "+output);
			System.exit(2);
		}
		int width=xmax+xoffset;
		int height=ymax+yoffset;
		pw.println("P3");
		pw.println(width+" "+height);
		pw.println("255");
		for(int i=0;i<height;i++){
			for(int j=0;j<width;j++){
				pw.println(imageint[(width-1)-j][i][0]+" "+imageint[(width-1)-j][i][1]+" "+imageint[(width-1)-j][i][2]);
			}
		}
		pw.close();
	}
	
	public void readCamera(String file){
		File file2 = new File(file);
		Scanner f=null;
		try {
			f = new Scanner(file2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File does not exist! Exiting");
			System.exit(1);
		}
		float[] FP={f.nextFloat(),f.nextFloat(),f.nextFloat()};
		float[] LAP={f.nextFloat(),f.nextFloat(),f.nextFloat()};
		float[] VUP={f.nextFloat(),f.nextFloat(),f.nextFloat()};
		float FL=f.nextFloat();
		cam=new Camera(FP,LAP,VUP,FL);
		xmin=f.nextInt();
		ymin=f.nextInt();
		xmax=f.nextInt();
		ymax=f.nextInt();
		if(xmin>xmax||ymin>ymax){
			System.out.println("Invalid camera paramaters!");
			System.exit(3);
		}
		xoffset=xmin*-1;
		yoffset=ymin*-1;
		
		
	}

	public void readMaterial(String file){
		File file2 = new File(file);
		Scanner f=null;
		try {
			f = new Scanner(file2);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("File does not exist! Exiting");
			System.exit(1);
		}
		while(f.hasNext()){
			String temp=f.next();
			if(temp.startsWith("L")){
				float[] RGB = new float[3];
				for(int i=0;i<3;i++){
					temp=f.next();
					RGB[i]=Float.parseFloat(temp);
				}
				temp=f.next();
				if(temp.startsWith("A")){
					f.next();
					f.next();
					ambient=RGB;
				}
				else{
					float[] lightLoc=new float[3];
					lightLoc[0]=Float.parseFloat(temp);
					for(int i=0;i<2;i++){
						temp=f.next();
						lightLoc[i+1]=Float.parseFloat(temp);
					}
					if(Lights==null){
						Lights=new float[1][6];
						Lights[0][0]=RGB[0];
						Lights[0][1]=RGB[1];
						Lights[0][2]=RGB[2];
						Lights[0][3]=lightLoc[0];
						Lights[0][4]=lightLoc[1];
						Lights[0][5]=lightLoc[2];
					}else{
						float tempvar[][]=new float[Lights.length+1][6];
						for(int i=0;i<Lights.length;i++){
							tempvar[i][0]=Lights[i][0];
							tempvar[i][1]=Lights[i][1];
							tempvar[i][2]=Lights[i][2];
							tempvar[i][3]=Lights[i][3];
							tempvar[i][4]=Lights[i][4];
							tempvar[i][5]=Lights[i][5];
						}
						tempvar[Lights.length][0]=RGB[0];
						tempvar[Lights.length][1]=RGB[1];
						tempvar[Lights.length][2]=RGB[2];
						tempvar[Lights.length][3]=lightLoc[0];
						tempvar[Lights.length][4]=lightLoc[1];
						tempvar[Lights.length][5]=lightLoc[2];
						Lights=tempvar;
					}
					
				}
			}
			else if(temp.startsWith("M")){
				temp=f.next();
				int objnum=Integer.parseInt(temp);
				temp=f.next();
				int minAffect=Integer.parseInt(temp);
				temp=f.next();
				int maxAffect=Integer.parseInt(temp);
				float[] lamb=new float[3];
				for(int i=0;i<3;i++){
					temp=f.next();
					lamb[i]=Float.parseFloat(temp);
				}
				temp=f.next();
				rfc=Float.parseFloat(temp);
				temp=f.next();
				shine=Float.parseFloat(temp);
				collection[objnum].setFaceProps(minAffect, maxAffect, lamb, rfc, shine);
			}
		}
		
	}
	
	public void readModels(String[] input){
		collection=new Model[input.length];
		for(int z=0;z<input.length;z++){
			File file = new File(input[z]);
			Scanner f=null;
			try {
				f = new Scanner(file);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				System.out.println("File does not exist! Exiting");
				System.exit(1);
			}
			String header="";
			String read="";
			int spacenum=0;
			int vertexnum=0;
			int facenum=0;
			while(f.hasNext()){
				read=f.nextLine();
				header+=read+"\r\n";
				if(read.contains("property"))spacenum++;
				if(read.contains("element vertex"))vertexnum=Integer.parseInt(read.substring(15));
				if(read.contains("element face"))facenum=Integer.parseInt(read.substring(13));
				if(read.contains("end_header"))break;
			}
			float[][] points=new float[3][vertexnum];
			for(int i=0; i<vertexnum;i++){
				for(int j=0; j<3;j++){
					points[j][i]=f.nextFloat();
				}
			}
			int width=f.nextInt();
			int[][] faces=new int[width][facenum];
			for(int i=0;i<facenum;i++){
				for(int j=0; j<width;j++){
					faces[j][i]=f.nextInt();
				}
				if(i!=facenum-1)f.nextInt();
			}
			collection[z]=new Model(points, faces);
		}
	}
	
	public Model[] getCollection(){
		return collection;
	}
	
	public void normalizeImage(){
		float min=0;
		float max=0;
		for(int i=0;i<image.length;i++){
			for(int j=0;j<image[0].length;j++){
				if(image[i][j][0]<min)min=image[i][j][0];
				if(image[i][j][0]>max)max=image[i][j][0];
				if(image[i][j][1]<min)min=image[i][j][1];
				if(image[i][j][1]>max)max=image[i][j][1];
				if(image[i][j][2]<min)min=image[i][j][2];
				if(image[i][j][2]>max)max=image[i][j][2];
			}
		}
		float scale=255/(max+min);
		for(int i=0;i<image.length;i++){
			for(int j=0;j<image[0].length;j++){
				image[i][j][0]=image[i][j][0]+min;
				image[i][j][0]=image[i][j][0]*scale;
				image[i][j][1]=image[i][j][1]+min;
				image[i][j][1]=image[i][j][1]*scale;
				image[i][j][2]=image[i][j][2]+min;
				image[i][j][2]=image[i][j][2]*scale;
				
			}
		}
		
	}
	
	public void imageInt(){
		imageint=new int[image.length][image[0].length][image[0][0].length];
		for(int i=0;i<image.length;i++){
			for(int j=0;j<image[0].length;j++){
				imageint[i][j][0]=Math.round(image[i][j][0]);
				imageint[i][j][1]=Math.round(image[i][j][1]);
				imageint[i][j][2]=Math.round(image[i][j][2]);
				
			}
		}
	}
	
	public void imageAvgAA(){
		image=new float[xmax+xoffset][ymax+yoffset][3];
		for(int i=0;i<image.length;i++){
			for(int j=0;j<image[0].length;j++){
				image[i][j][0]=(image1[i][j][0]+image2[i][j][0]+image3[i][j][0]+image4[i][j][0]+image5[i][j][0])/5;
				image[i][j][1]=(image1[i][j][1]+image2[i][j][1]+image3[i][j][1]+image4[i][j][1]+image5[i][j][1])/5;
				image[i][j][2]=(image1[i][j][2]+image2[i][j][2]+image3[i][j][2]+image4[i][j][2]+image5[i][j][2])/5;
				
			}
		}
	}
	
	public void makeImage(int numrun){
		image=new float[xmax+xoffset][ymax+yoffset][3];
		intersectedPoly=new float[xmax+xoffset][ymax+yoffset][3];
		for(int outer=xmin;outer<xmax;outer++){
			for(int inner=ymin;inner<ymax;inner++){
				for(int i=0;i<collection.length;i++){
					if(i==0){
						intersectedPoly[outer+xoffset][inner+yoffset]=cam.doesIntersect(collection[i],outer,inner);
						if(intersectedPoly[outer+xoffset][inner+yoffset][0]!=-1)intersectedPoly[outer+xoffset][inner+yoffset][0]=i;
						
					}
					else if(intersectedPoly[outer+xoffset][inner+yoffset][0]==-1){
						intersectedPoly[outer+xoffset][inner+yoffset]=cam.doesIntersect(collection[i],outer,inner);
						if(intersectedPoly[outer+xoffset][inner+yoffset][0]!=-1)intersectedPoly[outer+xoffset][inner+yoffset][0]=i;
					}
					else{
						float temp[]=cam.doesIntersect(collection[i],outer,inner);
						if(temp[2]<intersectedPoly[outer+xoffset][inner+yoffset][2]){
							intersectedPoly[outer+xoffset][inner+yoffset]=temp;
							if(intersectedPoly[outer+xoffset][inner+yoffset][0]!=-1)intersectedPoly[outer+xoffset][inner+yoffset][0]=i;
						}
					}
				}
			}
		}
		
		
		for(int outer=xmin;outer<xmax;outer++){
			for(int inner=ymin;inner<ymax;inner++){
				if(intersectedPoly[outer+xoffset][inner+yoffset][0]>=0){
					image[outer+xoffset][inner+yoffset]=collection[(int)intersectedPoly[outer+xoffset][inner+yoffset][0]].ambientCalc((int)intersectedPoly[outer+xoffset][inner+yoffset][1], ambient);
					float[] intersectionPoint=cam.getIntersectionPoint(intersectedPoly[outer+xoffset][inner+yoffset][2], outer, inner);
					float[] V=cam.getVVect(outer, inner);
					if(!(Lights==null)){
					for(int i=0;i<Lights.length;i++){
						temp=collection[(int)intersectedPoly[outer+xoffset][inner+yoffset][0]].diffuseCalc((int)intersectedPoly[outer+xoffset][inner+yoffset][1], Lights[i], intersectionPoint);
						temp2=collection[(int)intersectedPoly[outer+xoffset][inner+yoffset][0]].specularCalc((int)intersectedPoly[outer+xoffset][inner+yoffset][1], Lights[i], intersectionPoint, V, rfc, shine);
						image[outer+xoffset][inner+yoffset][0]+=(temp[0]+temp2[0]);
						image[outer+xoffset][inner+yoffset][1]+=(temp[1]+temp2[1]);
						image[outer+xoffset][inner+yoffset][2]+=(temp[2]+temp2[2]);
					}
					}
				}
				
			}
		}
		for(int outer=xmin;outer<xmax;outer++){
			for(int inner=ymin;inner<ymax;inner++){
				if(intersectedPoly[outer+xoffset][inner+yoffset][0]>=0){
					cam.getIntersectionPoint(intersectedPoly[outer+xoffset][inner+yoffset][2],outer,inner);
				}
				
			}
		}
		
		if(numrun==0){
			image1=image;
		}else if(numrun==1){
			image2=image;
		}else if(numrun==2){
			image3=image;
		}else if(numrun==3){
			image4=image;
		}else if(numrun==4){
			image5=image;
		}
		
		
	}

}
