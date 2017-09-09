import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.io.File;
import java.util.Date;
import java.util.Scanner;
public class DavisBase {
	static int pageSize=512;
	static String prompt = "davisql> ";

	static Scanner scanner = new Scanner(System.in).useDelimiter(";");
	private static RandomAccessFile davisbaseTablesCatalog;
	private static RandomAccessFile davisbaseColumnsCatalog;
	
	
    public static void main(String[] args) {
    	File dataDir = new File("data");
		if(dataDir.mkdir()){
			System.out.println("initializing database...");
			System.out.println();
			initializeDataStore();
		}
		splashScreen();
		String userCommand = ""; 
		while(!userCommand.equals("exit")) {
			System.out.print(prompt);
			userCommand = scanner.next().replace("\n", "").replace("\r", "").trim().toLowerCase();
			parseUserCommand(userCommand);
		}
		System.out.println("Exiting...");
		
	}
	public static void splashScreen() {
		System.out.println(line("-",80));
        System.out.println("Welcome to DavisBaseLite"); // Display the string.
        
        String welcomeMessage="";
        welcomeMessage += "Your current login time is:";
		welcomeMessage += new java.util.Date();
		System.out.println(welcomeMessage);
		System.out.println();
		welcomeMessage="";
		welcomeMessage += "\nThis lite sql version allows you to do the following operations:\n";
//		welcomeMessage +="1. SHOW SCHEMAS\n";
//		welcomeMessage +="2. CREATE SCHEMA\n";
		//welcomeMessage +="3. USE SCHEMA\n";
		welcomeMessage +="1. SHOW TABLES\n";
		welcomeMessage +="2. CREATE TABLE\n";
		welcomeMessage +="3. INSERT INTO TABLE\n";
		welcomeMessage +="4. DROP TABLE\n";
		welcomeMessage +="5. SELECT FROM WHERE\n";
		welcomeMessage += "If you wish to exit, please enter 'exit;' and press enter key\n";
		//welcomeMessage += "Or if you wish to see the syntax, please enter 'syntax;' and press enter key";
		System.out.println(welcomeMessage);
		System.out.println("Type \"help;\" to display supported commands.");
		System.out.println(line("-",80));
	}
	public static String line(String s,int num) {
		String a = "";
		for(int i=0;i<num;i++) {
			a += s;
		}
		return a;
	}
	
		public static void help() {
			System.out.println(line("*",80));
			System.out.println("SUPPORTED COMMANDS");
			System.out.println("All commands below are case insensitive");
			System.out.println();
			
			String syntaxes="";
//			syntaxes += "SHOW SCHEMAS;\n";
//			syntaxes += "***************************\n";
//			syntaxes += "/***CREATE SCHEMA***/\n";
//			syntaxes += "CREATE SCHEMA <schema_name>;\n";
//			syntaxes += "***************************\n";
//			syntaxes += "/***USE SCHEMA***/\n";
//			syntaxes += "USE <schema_name>;\n";
			//syntaxes += "***************************\n";
			syntaxes += "/***SHOW TABLES***/\n";
			syntaxes += "SHOW TABLES;\n";
			syntaxes += "***************************\n";
			syntaxes += "/***CREATE TABLE***/\n";
			syntaxes += "CREATE TABLE <table_name> (rowid int primary key, <col_name2> <col_type> not null, <col_name3> <col_type>..);\n";
			syntaxes += "***************************\n";
			syntaxes += "/***INSERT INTO TABLE***/\n";
			syntaxes += "INSERT INTO table_name (column_list) VALUES (value1,value2,value3,...);\n";
			syntaxes += "***************************\n";
			syntaxes += "/***DROP TABLE***/\n";
			syntaxes += "DROP TABLE <table_name>;\n";
			syntaxes += "***************************\n";
			syntaxes += "/***SELECT FROM WHERE***/\n";
			syntaxes += "SELECT * FROM <table_name>;\n";
			syntaxes += "***************************";
			syntaxes += "SELECT * FROM <table_name> WHERE <rowid> = val;\n";
			syntaxes += "***************************";
			
			System.out.println(syntaxes);
			System.out.println("Type <select * from tables;> and <select * from columns;> to view meta data");
//			System.out.println("\tSELECT * FROM table_name;                        Display all records in the table.");
//			System.out.println("\tSELECT * FROM table_name WHERE rowid = <value>;  Display records whose rowid is <id>.");
//			System.out.println("\tDROP TABLE table_name;                           Remove table data and its schema.");
			System.out.println("\tVERSION;              To know the database version.");
			System.out.println("\tHELP;                 Show supported syntaxes");
			System.out.println("\tEXIT;                 Exit ");
			System.out.println();
			System.out.println();
			System.out.println(line("*",80));
		}

	/** Display the DavisBase version */
	public static void version() {
		System.out.println("DavisBaseLite v1.0\n");
	}
	
	static void initializeDataStore() {

		/** Create data directory at the current OS location to hold */
		try {
			File dataDir = new File("data");
			dataDir.mkdir();
			String[] oldTableFiles;
			oldTableFiles = dataDir.list();
			for (int i=0; i<oldTableFiles.length; i++) {
				File anOldFile = new File(dataDir, oldTableFiles[i]); 
				anOldFile.delete();
			}
		}
		catch (SecurityException se) {
			System.out.println("Unable to create data container directory");
			System.out.println(se);
		}
		try {
			davisbaseTablesCatalog = new RandomAccessFile("data/davisbase_tables.tbl", "rw");
			davisbaseTablesCatalog.setLength(pageSize);
			davisbaseTablesCatalog.seek(0);
			davisbaseTablesCatalog.write(0x0D);
			davisbaseTablesCatalog.write(0x02);
			int size1=24;
			int[] offset=new int[2];
			int size2=25;
			offset[0]=pageSize-size1;
			offset[1]=offset[0]-size2;
			davisbaseTablesCatalog.writeShort(offset[1]);
			davisbaseTablesCatalog.writeShort(offset[0]);
			davisbaseTablesCatalog.writeShort(offset[1]);
			davisbaseTablesCatalog.seek(offset[0]);
			davisbaseTablesCatalog.writeShort(20);
			davisbaseTablesCatalog.writeInt(1); 
			davisbaseTablesCatalog.writeByte(1);
			davisbaseTablesCatalog.writeByte(28);
			davisbaseTablesCatalog.writeBytes("davisbase_tables");
			davisbaseTablesCatalog.seek(offset[1]);
			davisbaseTablesCatalog.writeShort(21);
			davisbaseTablesCatalog.writeInt(2); 
			davisbaseTablesCatalog.writeByte(1);
			davisbaseTablesCatalog.writeByte(29);
			davisbaseTablesCatalog.writeBytes("davisbase_columns");
		}
		catch (Exception e) {
			System.out.println("Unable to create the database_tables file");
			System.out.println(e);
		}
		try {
			davisbaseColumnsCatalog = new RandomAccessFile("data/davisbase_columns.tbl", "rw");
			davisbaseColumnsCatalog.setLength(pageSize);
			davisbaseColumnsCatalog.seek(0);       
			davisbaseColumnsCatalog.writeByte(0x0D);
			davisbaseColumnsCatalog.writeByte(0x09); 
			int[] offset=new int[10];
			offset[0]=pageSize-43;
			offset[1]=offset[0]-47;
			offset[2]=offset[1]-44;
			offset[3]=offset[2]-48;
			offset[4]=offset[3]-49;
			offset[5]=offset[4]-47;
			offset[6]=offset[5]-57;
			offset[7]=offset[6]-49;
			offset[8]=offset[7]-49;
			davisbaseColumnsCatalog.writeShort(offset[8]);
			for(int i=0;i<9;i++)
				davisbaseColumnsCatalog.writeShort(offset[i]);
			davisbaseColumnsCatalog.seek(offset[0]);
			davisbaseColumnsCatalog.writeShort(34);
			davisbaseColumnsCatalog.writeInt(1); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(28);
			davisbaseColumnsCatalog.writeByte(17);
			davisbaseColumnsCatalog.writeByte(15);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(15);
			davisbaseColumnsCatalog.writeBytes("davisbase_tables");
			davisbaseColumnsCatalog.writeBytes("rowid");
			davisbaseColumnsCatalog.writeBytes("INT");
			davisbaseColumnsCatalog.writeByte(1);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeBytes("PRI");
			
			davisbaseColumnsCatalog.seek(offset[1]);
			davisbaseColumnsCatalog.writeShort(38);
			davisbaseColumnsCatalog.writeInt(2); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(28);
			davisbaseColumnsCatalog.writeByte(22);
			davisbaseColumnsCatalog.writeByte(16);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(0);
			davisbaseColumnsCatalog.writeBytes("davisbase_tables");
			davisbaseColumnsCatalog.writeBytes("table_name");
			davisbaseColumnsCatalog.writeBytes("TEXT");
			davisbaseColumnsCatalog.writeByte(2);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeByte(0);
			
			davisbaseColumnsCatalog.seek(offset[2]);
			davisbaseColumnsCatalog.writeShort(35);
			davisbaseColumnsCatalog.writeInt(3); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(29);
			davisbaseColumnsCatalog.writeByte(17);
			davisbaseColumnsCatalog.writeByte(15);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(15);
			davisbaseColumnsCatalog.writeBytes("davisbase_columns");
			davisbaseColumnsCatalog.writeBytes("rowid");
			davisbaseColumnsCatalog.writeBytes("INT");
			davisbaseColumnsCatalog.writeByte(1);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeBytes("PRI");
			
			davisbaseColumnsCatalog.seek(offset[3]);
			davisbaseColumnsCatalog.writeShort(39);
			davisbaseColumnsCatalog.writeInt(4); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(29);
			davisbaseColumnsCatalog.writeByte(22);
			davisbaseColumnsCatalog.writeByte(16);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(0);
			davisbaseColumnsCatalog.writeBytes("davisbase_columns");
			davisbaseColumnsCatalog.writeBytes("table_name");
			davisbaseColumnsCatalog.writeBytes("TEXT");
			davisbaseColumnsCatalog.writeByte(2);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeByte(0);
			
			davisbaseColumnsCatalog.seek(offset[4]);
			davisbaseColumnsCatalog.writeShort(40);
			davisbaseColumnsCatalog.writeInt(5); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(29);
			davisbaseColumnsCatalog.writeByte(23);
			davisbaseColumnsCatalog.writeByte(16);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(0);
			davisbaseColumnsCatalog.writeBytes("davisbase_columns");
			davisbaseColumnsCatalog.writeBytes("column_name");
			davisbaseColumnsCatalog.writeBytes("TEXT");
			davisbaseColumnsCatalog.writeByte(3);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeByte(0);
			
			davisbaseColumnsCatalog.seek(offset[5]);
			davisbaseColumnsCatalog.writeShort(38);
			davisbaseColumnsCatalog.writeInt(6); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(29);
			davisbaseColumnsCatalog.writeByte(21);
			davisbaseColumnsCatalog.writeByte(16);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(0);
			davisbaseColumnsCatalog.writeBytes("davisbase_columns");
			davisbaseColumnsCatalog.writeBytes("data_type");
			davisbaseColumnsCatalog.writeBytes("TEXT");
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeByte(0);
			
			davisbaseColumnsCatalog.seek(offset[6]);
			davisbaseColumnsCatalog.writeShort(48);
			davisbaseColumnsCatalog.writeInt(7); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(29);
			davisbaseColumnsCatalog.writeByte(28);
			davisbaseColumnsCatalog.writeByte(19);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(0);
			davisbaseColumnsCatalog.writeBytes("davisbase_columns");
			davisbaseColumnsCatalog.writeBytes("ordinal_position");
			davisbaseColumnsCatalog.writeBytes("TINYINT");
			davisbaseColumnsCatalog.writeByte(5);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeByte(0);
			
			davisbaseColumnsCatalog.seek(offset[7]);
			davisbaseColumnsCatalog.writeShort(40);
			davisbaseColumnsCatalog.writeInt(8); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(29);
			davisbaseColumnsCatalog.writeByte(23);
			davisbaseColumnsCatalog.writeByte(16);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(14);
			davisbaseColumnsCatalog.writeByte(0);
			davisbaseColumnsCatalog.writeBytes("davisbase_columns");
			davisbaseColumnsCatalog.writeBytes("is_nullable");
			davisbaseColumnsCatalog.writeBytes("TEXT");
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeBytes("NO");
			davisbaseColumnsCatalog.writeByte(0);
			
			davisbaseColumnsCatalog.seek(offset[8]);
			davisbaseColumnsCatalog.writeShort(40);
			davisbaseColumnsCatalog.writeInt(9); 
			davisbaseColumnsCatalog.writeByte(6);
			davisbaseColumnsCatalog.writeByte(29);
			davisbaseColumnsCatalog.writeByte(22);
			davisbaseColumnsCatalog.writeByte(16);
			davisbaseColumnsCatalog.writeByte(4);
			davisbaseColumnsCatalog.writeByte(15);
			davisbaseColumnsCatalog.writeByte(0);
			davisbaseColumnsCatalog.writeBytes("davisbase_columns");
			davisbaseColumnsCatalog.writeBytes("column_key");
			davisbaseColumnsCatalog.writeBytes("TEXT");
			davisbaseColumnsCatalog.writeByte(7);
			davisbaseColumnsCatalog.writeBytes("YES");
			davisbaseColumnsCatalog.writeByte(0);
		}
		catch (Exception e) {
			System.out.println("Unable to create the database_columns file");
			System.out.println(e);
		}
	}
	public static void moveLeafRow(int rownum,RandomAccessFile file, int oldpage, int newpage){
		try{
			file.seek(512*(oldpage-1)+2+2*rownum);
			int offset1=file.readShort();
			file.seek(offset1+512*(oldpage-1));
			int payload=file.readShort();
			file.seek(offset1+6+512*(oldpage-1));
			int attributes=file.read();
			int size=payload+attributes+3;
			file.seek(512*(newpage-1)+1);
			int num=file.read();
			file.seek(512*(newpage-1)+1);
			file.write(num+1);
			file.seek(512*(newpage-1)+2);
			int offset2=file.readShort();
			offset2=offset2-size;
			file.seek(512*(newpage-1)+2);
			file.writeShort(offset2);
			file.seek(512*(newpage-1)+4+2*num);
			file.writeShort(offset2);
			for(int i=0;i<size;i++){
				file.seek(offset1+i+512*(oldpage-1));
				Byte value=file.readByte();
				file.seek(offset2+i+512*(newpage-1));
				file.writeByte(value);				
			}
		}
		catch (Exception e) {
			System.out.println("Unable to move row to the new leaf page");
			System.out.println(e);
		}
	}
	public static int addLeafRow(int load, RandomAccessFile file, int page,int rowid){
		try{
			file.seek((page-1)*512+2);
			int offset=file.readShort();
			file.seek((page-1)*512+1);
			int num=file.read();
			file.seek((page-1)*512+offset+7);
			int attributes=file.read();
			int size=load+attributes+3;
			int remain=offset-2*num-4;
			if(remain>=size){
				return page;
			}
			else{			
				long length=file.length();
				int newpage=(int) (length/512)+1;
				file.setLength(512*newpage);
				file.seek(512*(newpage-1));
				file.write(0x0D);
				file.seek(512*(newpage-1)+1);
				file.write(0);
				file.seek(512*(newpage-1)+2);
				file.writeShort(512);
				int midnum=(num+1)/2;
				file.seek(512*(page-1)+2+2*midnum);
				int midoffset=file.readShort();
				file.seek(midoffset+2+512*(page-1));
				int midkey=file.readInt();
				for(int i=1;i<midnum;i++){
					moveLeafRow(i,file,page,newpage);
				}
				file.seek(512*(page-1)+1);
				file.write(num-midnum+1);
				for(int i=midnum;i<=num;i++){
					file.seek(512*(page-1)+2+2*i);
					int offset2=file.readShort();
					file.seek(512*(page-1)+2+2*(i-midnum+1));
					file.writeShort(offset2);
				}
				sortleafpage(file,page);
				if(page==1){
					length=file.length();
					int page2=(int) (length/512)+1;
					file.setLength(page2*512);
					file.seek((page2-1)*512);
					file.write(0x0D);
					file.seek((page2-1)*512+1);
					file.write(0);
					file.seek((page2-1)*512+2);
					file.writeShort(512);
					for(int i=1;i<=num-midnum+1;i++){
						moveLeafRow(i,file,page,page2);
					}
					file.seek(0);
					file.write(0x05);
					file.write(1);
					file.writeShort(0x01F8);
					file.writeInt(page2);
					file.writeShort(0x01F8);
					file.seek(0x01F8);
					file.writeInt(newpage);			
					file.writeInt(midkey);
					if(rowid<midkey)
						return newpage;
					else
						return page2;
				}
				else{
					int parentpage=findparent(file,page,1);
					addInteriorRow(file,parentpage,midkey,newpage);
					if(rowid<midkey)
						return newpage;
					else
						return page;
				}
				
			}
			
		}
		catch (Exception e) {
			System.out.println("Unable to add row");
			System.out.println(e);
			return -1;
		}
		
		
	}
	public static void addInteriorRow(RandomAccessFile file, int page, int rowid, int childpage){
		try{
			file.seek(512*(page-1)+2);
			int offset=file.readShort();
			file.seek(512*(page-1)+1);
			int num=file.read();
			int remain=offset-8-2*num;
			if(remain>=10){
				file.seek(512*(page-1)+1);
				file.write(num+1);
				file.seek(512*(page-1)+2);
				file.writeShort(offset-8);
				int num1=0;
				for(int i=0;i<num;i++){
					file.seek(512*(page-1)+8+2*i);
					int offset1=file.readShort();
					file.seek(offset1+4+512*(page-1));
					int key=file.readInt();
					if(key>rowid){
						num1=i+1;
						break;
					}
				}
				if(num1==0){
					file.seek(512*(page-1)+8+2*num);
					file.writeShort(offset-8);
					file.seek(offset-8+512*(page-1));
					file.writeInt(childpage);
					file.writeInt(rowid);
				}
				else{
					int offset1=offset-8;
					file.seek(offset1+512*(page-1));
					file.writeInt(childpage);
					file.writeInt(rowid);
					for(int j=num;j>=num1;j--){
						file.seek(512*(page-1)+8+2*(j-1));
						int pointer1=file.readShort();
						file.writeShort(pointer1);
					}
					file.seek(512*(page-1)+6+2*num1);
					file.writeShort(offset-8);
				}
				
			}
			else{
				long length=file.length();
				int newpage=(int) (length/512)+1;
				file.setLength(newpage*512);
				file.seek((newpage-1)*512);
				file.write(0x05);
				int midnum=(num+2)/2;
				file.seek((page-1)*512+6+2*midnum);
				int midoffset=file.readShort();
				file.seek(midoffset+512*(page-1));
				int midpage=file.readInt();
				file.seek(midoffset+4+512*(page-1));
				int midkey=file.readInt();
				file.seek((newpage-1)*512+1);
				file.write(0);
				file.writeShort(512);
				file.writeInt(0); 
				for(int i=1;i<midnum;i++){
					file.seek((page-1)*512+6+2*i);
					int offset2=file.readShort();
					file.seek(offset2+512*(page-1));
					int page2=file.readInt();
					int key2=file.readInt();
					addInteriorRow(file,newpage,key2,page2);
				}
				file.seek(512*(newpage-1)+4);
				file.writeInt(midpage);
				file.seek(512*(page-1)+1);
				file.write(0);
				file.writeShort(512);
				int[] key=new int[100];
				int[] pagenum=new int[100];
				for(int i=midnum+1;i<=num;i++){
					file.seek((page-1)*512+6+2*i);
					offset=file.readShort();
					file.seek(offset+512*(page-1));
					pagenum[i]=file.readInt();
					key[i]=file.readInt();
				}
				for(int i=midnum+1;i<=num;i++){
					addInteriorRow(file,page,key[i],pagenum[i]);
				}
				if(page==1){
					length=file.length();
					int page2=(int) (length/512)+1;
					file.setLength(page2*512);
					file.seek((page2-1)*512);
					file.write(0x05);
					file.write(0);
					file.writeShort(512);
					for(int i=midnum+1;i<=num;i++){
						addInteriorRow(file,page2,key[i],pagenum[i]);
					}
					file.seek(512*(page-1)+4);
					int right=file.readInt();
					file.seek(512*(page2-1)+4);
					file.writeInt(right);
					if(rowid<midkey){
						addInteriorRow(file,newpage,rowid,childpage);
					}
					else{
						addInteriorRow(file,page2,rowid,childpage);
					}
					file.seek(0);
					file.write(0x05);
					file.write(1);
					file.writeShort(512-8);
					file.writeInt(page2);
					file.writeShort(0x01F8);
					file.seek(0x01F8);
					file.writeInt(newpage);			
					file.writeInt(midkey);	
				}
				else{
					if(rowid<midkey){
						addInteriorRow(file,newpage,rowid,childpage);
					}
					else{
					
						addInteriorRow(file,page,rowid,childpage);
					}
					int parentpage=findparent(file,page,1);
					addInteriorRow(file,parentpage,midkey,newpage);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Unable to add into interior");
			System.out.println(e);
		}
	}
	public static int findparent(RandomAccessFile file, int page, int rootpage){
		try{
			file.seek(512*(page-1));
			byte type=file.readByte();
			file.read();
			int offset=file.readShort();
			int rowid=-1;
			if(type==0x0D){
				file.seek(offset+2+512*(page-1));
				rowid=file.readInt();
			}
			else{
				file.seek(offset+4+512*(page-1));
				rowid=file.readInt();
			}
			int no=0;
			int pagenum=0;
			file.seek(512*(rootpage-1)+1);
			int num=file.read();
			for(int i=1;i<=num;i++){
				file.seek(512*(rootpage-1)+2*i+6);
				offset=file.readShort();
				file.seek(offset+4+512*(rootpage-1));
				int key=file.readInt();
				if(rowid<key){
					no=i;
					file.seek(offset+512*(rootpage-1));
					pagenum=file.readInt();
					if(pagenum==page)
						return rootpage;
					else
						break;
				}
				
			}
			if(no==0){
				file.seek(512*(rootpage-1)+4);
				pagenum=file.readInt();
				if(pagenum==page)
					return rootpage;
				else
					return findparent(file,page,pagenum);
			}
			else{
				return findparent(file,page,pagenum);
			}
		}
		catch (Exception e) {
			System.out.println("Unable to find parent");
			System.out.println(e);
			return -1;
		}
	}
	public static int findpage(RandomAccessFile file,int rowid,int page){
		try{
			file.seek(512*(page-1));
			byte type=file.readByte();
			if(type==0x0D){
				return page;
			}
			else{
				file.seek(512*(page-1)+1);
				int num=file.readByte();
				int no=-1;
				for(int i=1;i<=num;i++){
					file.seek(512*(page-1)+2*i+6);									
					int offset=file.readShort();
					file.seek(offset+4+512*(page-1));
					int key=file.readInt();
					if(key>rowid){
						no=i;	
						break;
					}
				}
				if(no!=-1){
					file.seek(512*(page-1)+2*no+6);
					int offset=file.readShort();
					file.seek(offset+512*(page-1));
					int pagenum=file.readInt();
					int a=findpage(file,rowid,pagenum);
					return a;
				}
				else{
					file.seek(512*(page-1)+4);
					int pagenum=file.readInt();
					int a=findpage(file,rowid,pagenum);
					return a;
				}
			}
		}
		catch (Exception e) {
			System.out.println("Unable to find page");
			System.out.println(e);
			return -1;
		}
	}
	public static int findNoInLeaf(RandomAccessFile file,int rowid,int page){
		try{
			file.seek(512*(page-1)+1);
			int no=-1;
			int num=file.readByte();
			for(int i=1;i<=num;i++){
				file.seek(512*(page-1)+2+2*i);
				int offset=file.readShort();
				file.seek(offset+2+512*(page-1));
				int key=file.readInt();
				if(key==rowid)
					no=i;
			}
			return no;
		}
		catch (Exception e) {
			System.out.println("Unable to find no in leaf");
			System.out.println(e);
			return -1;
		}
	}
	public static void printRowidEqual(RandomAccessFile file,int rowid,int[] order){
		try{
			System.out.print(rowid+"\t");
			int page=findpage(file,rowid,1);
			int no=findNoInLeaf(file,rowid,page);
			file.seek(512*(page-1)+2*no+2);
			int offset=file.readShort();
			file.seek(offset+6+512*(page-1));
			int num=file.readByte();
			byte[] type=new byte[num];
			for(int i=0;i<num;i++)
				type[i]=file.readByte();
			int index=0;
			int max=order[order.length-1];
			file.seek(offset+7+num+512*(page-1));
			int a=0;
			for(int i=0;i<max;i++){
				switch(type[i]){
					case 0x00:
						file.readByte();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s","null"));
							index++;
						}
						break;
					case 0x01:
						file.readShort();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s","null"));
							index++;
						}
						break;
					case 0x02:
						file.readInt();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s","null"));
							index++;
						}
						break;
					case 0x03:
						file.readLong();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s","null"));
							index++;
						}
						break;
					case 0x04:
						a=file.readByte();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",a));
							index++;
						}
						break;
					case 0x05:
						a=file.readShort();
						if(order[index]==i+1){
							index++;
						}
						break;
					case 0x06:
						a=file.readInt();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",a));
							index++;
						}
						break;
					case 0x07:
						long b=file.readLong();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",b));
							index++;
						}
						break;
					case 0x08:
						float c=file.readFloat();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",c));
							index++;
						}
						break;
					case 0x09:
						double d=file.readDouble();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",d));
							index++;
						}
						break;
					case 0x0A:
						Long mill=file.readLong();
						java.util.Date date = new Date(mill);
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",date));
							index++;
						}
						break;
					case 0x0B:
						mill=file.readLong();
						date = new Date(mill);
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",date));
							index++;
						}
						break;
					default:
						int length=type[i]-12;
						byte[] e=new byte[length];
						for(int f=0;f<length;f++)
							e[f]=file.readByte();
						if(order[index]==i+1){
							System.out.print(String.format("%-20s",new String(e)));
							index++;
						}					
				}
			}
			System.out.println();
		}
		catch (Exception e) {
			System.out.println("Unable to query when rowid = value");
			System.out.println(e);
		}
	}
	public static int findmaxrowid(RandomAccessFile file, int page){
		try{
			file.seek(512*(page-1));
			int type=file.readByte();
			int num=file.readByte();
			if(type==0x0D){
				file.seek(512*(page-1)+2+2*num);
				int offset=file.readShort();
				file.seek(offset+2+512*(page-1));
				return file.readInt();
			}
			else{
				file.seek(512*(page-1)+4);
				int pageno=file.readInt();
				int a=findmaxrowid(file,pageno);
				return a;
			}
		}
		catch (Exception e) {
			System.out.println("Unable to find max rowid ");
			return -1;
		}
	}
	public static int countsize(byte type){
		int size=0;
		switch(type){
		case 0x00:
			size=1;
			break;
		case 0x01:
			size=2;
			break;
		case 0x02:
			size=4;
			break;
		case 0x03:
			size=8;
			break;
		case 0x04:
			size=1;
			break;
		case 0x05:
			size=2;
			break;
		case 0x06:
			size=4;
			break;
		case 0x07:
			size=8;
			break;
		case 0x08:
			size=4;
			break;
		case 0x09:
			size=8;
			break;
		case 0x0A:
			size=8;
			break;
		case 0x0B:
			size=8;
			break;
		default:
			size=type-12;
			break;
		}
		return size;
	}
	public static byte[] findtype(String tablename, int num){
		try{
			RandomAccessFile file= new RandomAccessFile("data/davisbase_columns.tbl", "rw");
			byte[] types=new byte[num];
			int i=0;
			String table_name="";
			String[] type_name=new String[num];
			while(!table_name.equals("davisbase_"+tablename)){
				i++;
				int page=findpage(file,i,1);
				int no=findNoInLeaf(file,i,page);
				file.seek(512*(page-1)+2*no+2);
				int offset=file.readShort();
				file.seek(offset+7+512*(page-1));
				int length1=file.read()-12;
				file.seek(offset+13+512*(page-1));
				byte[] a=new byte[length1];
				for(int j=0;j<length1;j++)
					a[j]=file.readByte();
				table_name=new String(a);
			}
			i++;
			for(int j=0;j<num;j++){
				int page=findpage(file,i,1);
				int no=findNoInLeaf(file,i,page);
				file.seek(512*(page-1)+2*no+2);
				int offset=file.readShort();
				file.seek(offset+7+512*(page-1));
				int length1=file.read()-12;
				file.seek(offset+8+512*(page-1));
				int length2=file.read()-12;
				file.seek(offset+9+512*(page-1));
				int length3=file.read()-12;
				file.seek(offset+13+length1+length2+512*(page-1));
				byte[] a=new byte[length3];
				for(int k=0;k<length3;k++)
					a[k]=file.readByte();
				type_name[j]=new String(a);
				i++;
			}
			for(int j=0;j<num;j++){
				switch(type_name[j]){
				case "null":
					types[j]=0 ;
					break;
				case "TINYINT":
					types[j]=4;
					break;
				case "SMALLINT":
					types[j]=5;
					break;
				case "INT":
					types[j]=6;
					break;
				case "BIGINT":
					types[j]=7;
					break;
				case "REAL":
					types[j]=8;
					break;
				case "DOUBLE":
					types[j]=9;
					break;
				case "DATETIME":
					types[j]=10;
					break;
				case "DATE":
					types[j]=11;
					break;
				default:
					types[j]=12;
					break;
				}
			}
			return types;
		}
		catch (Exception e) {
			System.out.println("Unable to find types");
			System.out.println(e);
			return null;
		}
	}
	public static void sortleafpage(RandomAccessFile file, int page){
		try{
			long length=file.length();
			int newpage=(int)(length/512+1);
			file.setLength(newpage*512);
			file.seek(512*(newpage-1));
			file.write(0x0D);
			file.write(0);
			file.writeShort(512);
			file.seek(512*(page-1)+1);
			int num=file.readByte();
			for(int i=1;i<=num;i++){
				moveLeafRow(i,file,page,newpage);
			}
			file.seek(512*(page-1));
			file.write(0x0D);
			file.write(0);
			file.writeShort(512);
			for(int i=1;i<=num;i++){
				moveLeafRow(i,file,newpage,page);
			}
			file.setLength((newpage-1)*512);
		}
		catch (Exception e) {
			System.out.println("Unable to sort the page");
			System.out.println(e);
		}
	}
	public static int[] findpayload(String[] column,byte[] types){
		int[] payload=new int[column.length];
		for(int i=0;i<column.length;i++){
			switch(types[i]){
			case 0x00:
				payload[i]=1;
				break;
			case 0x01:
				payload[i]=2;
				break;
			case 0x02:
				payload[i]=4;
				break;
			case 0x03:
				payload[i]=8;
				break;
			case 0x04:
				payload[i]=1;
				break;
			case 0x05:
				payload[i]=2;
				break;
			case 0x06:
				payload[i]=4;
				break;
			case 0x07:
				payload[i]=8;
				break;
			case 0x08:
				payload[i]=4;
				break;
			case 0x09:
				payload[i]=8;
				break;
			case 0x0A:
				payload[i]=8;
				break;
			case 0x0B:
				payload[i]=8;
				break;
			default:
				payload[i]=column[i].length();
			}
		}
		return payload;
	}
	public static void parseUserCommand (String userCommand) {
		String[] commandTokens = userCommand.split(" ");
		switch (commandTokens[0]) {
			case "show":
				parseUserCommand("select * from tables ");
				
				break;
			case "create":
				int first=userCommand.indexOf("table");
				int last=userCommand.indexOf("(");
			    String tablename=userCommand.substring(first+5,last).trim();
				RandomAccessFile file;
				try {
					file = new RandomAccessFile("data/davisbase_" + tablename+".tbl", "rw");
					file.setLength(pageSize);
					file.seek(0);
					file.write(0x0D);
					file.write(0x00);
					file.writeShort(pageSize);
					int length=tablename.length()+10;
					
					file=new RandomAccessFile("data/davisbase_tables.tbl", "rw"); 
					int max=findmaxrowid(file,1);
					int page1=findpage(file, max,1);
					int page=addLeafRow(length+8,file,page1,max+1);
					file.seek(512*(page-1)+1);
					int num=file.read();
					int offset=file.readShort();
					file.seek(512*(page-1)+1);
					file.write(++num);
					offset=offset-length-8;
			
					file.writeShort(offset);
					file.seek(512*(page-1)+2*num+2);
					file.writeShort(offset);
					file.seek(offset+512*(page-1));
					file.writeShort(length+4);
					file.writeInt(max+1);
					file.write(1);
					file.write(12+length);
					file.writeBytes("davisbase_"+tablename);
					
					file=new RandomAccessFile("data/davisbase_columns.tbl", "rw"); 
					first = userCommand.indexOf("(");
				    last = userCommand.lastIndexOf(")");
				    String content=userCommand.substring(first + 1, last).trim();
				    String[] columns = content.split(",");
				    String[] column1 = columns[0].split(" ");
				    int length1=column1[0].length();
				    int length2=column1[1].length();
				    int payload=4+length1+length2+length+1+2+3;
				    max=findmaxrowid(file,1);
					page1=findpage(file, max,1);
					page=addLeafRow(payload+9,file,page1,max+1);
					file.seek(512*(page-1)+1);
					num=file.read();
					offset=file.readShort();
					file.seek(512*(page-1)+1);
					file.write(++num);
					offset=offset-payload-9;
					file.writeShort(offset);
					file.seek(512*(page-1)+2*num+2);
					file.writeShort(offset);
					file.seek(offset+512*(page-1));
					file.writeShort(payload);
					file.writeInt(max+1);
					file.write(6);
					file.write(12+length);
					file.write(12+length1);
					file.write(12+length2);
					file.write(0x04);
					file.write(14);
					file.write(15);
				    file.writeBytes("davisbase_"+tablename);
				    file.writeBytes(column1[0]);
				    file.writeBytes(column1[1].toUpperCase());
				    file.write(1);
				    file.writeBytes("NO");
				    file.writeBytes("PRI");
				    int nums=columns.length;
				    for(int i=1;i<nums;i++){
				    	columns[i]=columns[i].trim();
				    	String[] column = columns[i].split(" ");
				    	length1=column[0].length();
					    length2=column[1].length();
					    if(column.length!=2)
					    	payload=4+length1+length2+length+1+2+1;
					    else
					    	payload=4+length1+length2+length+1+3+1;
					    max=findmaxrowid(file,1);
						page1=findpage(file, max,1);
						page=addLeafRow(payload+9,file,page1,max+1);
						file.seek(512*(page-1)+1);
						num=file.read();
						offset=file.readShort();
						file.seek(512*(page-1)+1);
						file.write(++num);
						offset=offset-payload-9;
						file.writeShort(offset);
						file.seek(512*(page-1)+2*num+2);
						file.writeShort(offset);
						file.seek(offset+512*(page-1));
						file.writeShort(payload);
						file.writeInt(max+1);
						file.write(6);
						file.write(12+length);
						file.write(12+length1);
						file.write(12+length2);
						file.write(0x04);
						if(column.length!=2)
							file.write(14);
					    else
					    	file.write(15);
						file.write(0);
						file.writeBytes("davisbase_"+tablename);
					    file.writeBytes(column[0]);
					    file.writeBytes(column[1].toUpperCase());
					    file.write(1+i);
					    if(column.length!=2)
					    	file.writeBytes("NO");
					    else
					    	file.writeBytes("YES");
					    file.write(0);
				    }
				    System.out.println("Table created");
					
				}
				catch (Exception e) {
					System.out.println("Unable to create " );
				};
				break;
			case "drop":
				try{
					tablename=commandTokens[2];
					file=new RandomAccessFile("data/davisbase_columns.tbl", "rw");
					String table_name="";
					int i=0;
					int max=findmaxrowid(file,1);
					while(!table_name.equals("davisbase_"+tablename)){
						i++;
						int page=findpage(file,i,1);
						int no=findNoInLeaf(file,i,page);
						file.seek(512*(page-1)+2*no+2);
						int offset=file.readShort();
						file.seek(offset+7+512*(page-1));
						int length1=file.read()-12;
						file.seek(offset+13+512*(page-1));
						byte[] a=new byte[length1];
						for(int j=0;j<length1;j++)
							a[j]=file.readByte();
						table_name=new String(a);
					}
					while(table_name.equals("davisbase_"+tablename)){
						int page=findpage(file,i,1);
						file.seek(512*(page-1)+1);
						int num=file.read();
						int no=findNoInLeaf(file,i,page);
						file.seek(512*(page-1)+1);
						file.write(num-1);
						if(no<num){
							for(int j=no+1;j<=num;j++){
								file.seek(512*(page-1)+2+2*j);
								int offset=file.readShort();
								file.seek(512*(page-1)+2*j);
								file.writeShort(offset);
							}
						}
						sortleafpage(file,page);
						i++;
						if(i>max)
							break;
						page=findpage(file,i,1);
						no=findNoInLeaf(file,i,page);
						file.seek(512*(page-1)+2*no+2);
						int offset=file.readShort();
						file.seek(offset+7+512*(page-1));
						int length1=file.read()-12;
						file.seek(offset+13+512*(page-1));
						byte[] a=new byte[length1];
						for(int j=0;j<length1;j++)
							a[j]=file.readByte();
						table_name=new String(a);
					}
					
					file=new RandomAccessFile("data/davisbase_tables.tbl", "rw");
					table_name="";
					i=0;
					while(!table_name.equals("davisbase_"+tablename)){
						i++;
						int page=findpage(file,i,1);
						int no=findNoInLeaf(file,i,page);
						file.seek(512*(page-1)+2*no+2);
						int offset=file.readShort();
						file.seek(offset+2+512*(page-1));
						file.seek(offset+7+512*(page-1));
						int length1=file.read()-12;
						file.seek(offset+8+512*(page-1));
						byte[] a=new byte[length1];
						for(int j=0;j<length1;j++)
							a[j]=file.readByte();
						table_name=new String(a);
					}
					int page=findpage(file,i,1);
					file.seek(512*(page-1)+1);
					int num=file.read();
					
					int no=findNoInLeaf(file,i,page);
					file.seek(512*(page-1)+1);
					file.write(num-1);
					if(no!=num){
						for(int j=no+1;j<=num;j++){
							file.seek(512*(page-1)+2+2*j);
							int offset=file.readShort();
							file.seek(512*(page-1)+2*j);
							file.writeShort(offset);
						}
					}

					sortleafpage(file,page);
					System.out.println("Table dropped");
				}
				catch (Exception e) {
					System.out.println("Unable to drop table " );
					System.out.println(e);
				};
				break;
			case "select":
				try{
					if(userCommand.indexOf("where")<0){
						String command=userCommand+" where rowid > 0 ";
						parseUserCommand (command);
					}
					else{
						first = userCommand.indexOf("select");
					    last = userCommand.lastIndexOf("from");
					    String attributes=userCommand.substring(first + 6, last).trim();
					    first = userCommand.indexOf("from");
					    last = userCommand.lastIndexOf("where");
					    tablename=userCommand.substring(first + 4, last).trim();
					    first = userCommand.indexOf("where");
					    last = userCommand.length();
					    String Str=userCommand.substring(first + 5, last).trim();
					    String[] str = Str.split(" ");
					    
						file=new RandomAccessFile("data/davisbase_" +tablename+".tbl", "rw");
						int page=findpage(file,1,1);
						file.seek(512*(page-1)+4);
						int offset=file.readShort();
						file.seek(offset+6+512*(page-1));
						int num=file.read();

						file=new RandomAccessFile("data/davisbase_columns.tbl", "rw");
						String table_name="";
						String column_name="";
						int i=0;

						while(!table_name.equals("davisbase_"+tablename)){
							i++;
							page=findpage(file,i,1);
							int no=findNoInLeaf(file,i,page);
							file.seek(512*(page-1)+2*no+2);
							offset=file.readShort();
							file.seek(offset+7+512*(page-1));
							int length1=file.read()-12;
							file.seek(offset+13+512*(page-1));
							byte[] a=new byte[length1];
							for(int j=0;j<length1;j++)
								a[j]=file.readByte();
							table_name=new String(a);
						}

						int startkey=i;
						int[] order;
						if(attributes.equals("*")){
							order=new int[num];
							for(int j=0;j<num;j++)
								order[j]=j+1;
							i++;
							System.out.print("rowid\t");
							for(int j=0;j<num;j++){
								page=findpage(file,i,1);
								int no=findNoInLeaf(file,i,page);
								file.seek(512*(page-1)+2*no+2);
								offset=file.readShort();
								file.seek(offset+7+512*(page-1));
								int length1=file.read()-12;
								file.seek(offset+8+512*(page-1));
								int length2=file.read()-12;
								file.seek(offset+13+length1+512*(page-1));
								byte[] a=new byte[length2];
								for(int k=0;k<length2;k++)
									a[k]=file.readByte();
								column_name=new String(a);
								System.out.print(String.format("%-20s",column_name));
								i++;
							}
							System.out.println();
						}
						else{
							System.out.print("rowid\t");
							String[] attribute = attributes.split(",");
							int length=attribute.length;
							order=new int[length];
							for(int j=0;j<length;j++){
								attribute[j]=attribute[j].trim();
								System.out.print(attribute[j]+"\t");
							}
							int index=0;
							for(int j=0;j<num;j++){
								page=findpage(file,i,1);
								int no=findNoInLeaf(file,i,page);
								file.seek(512*(page-1)+2*no+2);
								offset=file.readShort();
								file.seek(offset+7+512*(page-1));
								int length1=file.read()-12;
								file.seek(offset+8+512*(page-1));
								int length2=file.read()-12;
								file.seek(offset+7+num+length1+512*(page-1));
								byte[] a=new byte[length2];
								for(int k=0;k<length2;k++)
									a[k]=file.readByte();
								column_name=new String(a);
								if(column_name.equals(attribute[index])){
									order[index]=j;
									index++;
								}	
								i++;
								if(index==order.length)
									break;
							}
						}
						System.out.println();
						file=new RandomAccessFile("data/davisbase_" +tablename+".tbl", "rw");
						if(str[0].equals("rowid")){
							switch(str[1]){
							case "=":
								printRowidEqual(file,Integer.parseInt(str[2]),order);
								break;
							case ">":
								int max=findmaxrowid(file,1);
								for(int k=Integer.parseInt(str[2])+1;k<=max;k++){
									int pageno=findpage(file,k,1);
									if(pageno>0)
										printRowidEqual(file,k,order);
								}
								break;
							case "<":
								for(int k=1;k<Integer.parseInt(str[2]);k++){
									int pageno=findpage(file,k,1);
									if(pageno>0)
										printRowidEqual(file,k,order);
								}
								break;
							case "<=":
								for(int k=1;k<=Integer.parseInt(str[2]);k++){
									int pageno=findpage(file,k,1);
									if(pageno>0)
										printRowidEqual(file,k,order);
								}
								break;
							case ">=":
								max=findmaxrowid(file,1);
								for(int k=Integer.parseInt(str[2]);k<=max;k++){
									int pageno=findpage(file,k,1);
									if(pageno>0)
										printRowidEqual(file,k,order);
								}
								break;
							case "<>":
								max=findmaxrowid(file,1);
								for(int k=1;k<=max;k++){
									int pageno=findpage(file,k,1);
									if(pageno>0&&k!=Integer.parseInt(str[2]))
										printRowidEqual(file,k,order);
								}
								break;
							}
						}
						else{
							i=startkey;
							int no=-1;
							for(int j=0;j<=num;j++){
								page=findpage(file,i,1);
								file.seek(512*(page-1)+2*i+2);
								offset=file.readShort();
								file.seek(offset+7+512*(page-1));
								int length1=file.read()-12;
								file.seek(offset+8+512*(page-1));
								int length2=file.read()-12;
								file.seek(offset+7+num+length1+512*(page-1));
								byte[] a=new byte[length2];
								for(int k=0;k<length2;k++)
									a[k]=file.readByte();
								column_name=new String(a);
								if(column_name.equals(str[0])){
									no=j;
									break;
								}	
								i++;
							}
							int max=findmaxrowid(file,1);
							for(int k=1;k<=max;k++){
								int pageno=findpage(file,k,1);
								if(pageno>0){
									int a=findNoInLeaf(file,k,pageno);
									file.seek((pageno-1)*512+2+2*a);
									offset=file.readShort();
									int size=0;
									file.seek(offset+512*(pageno-1));
									for(int e=1;e<no;e++){
										file.seek(offset+6+e+512*(pageno-1));
										byte type=file.readByte();
										size+=countsize(type);
									}
									file.seek(offset+6+no+512*(pageno-1));
									byte type=file.readByte();
									int length=countsize(type);
									file.seek(offset+7+num+size+512*(pageno-1));
									byte[] st=new byte[length];
									for(int f=0;f<length;f++)
										st[f]=file.readByte();
									String attribute=new String(st);
									if(attribute.toLowerCase().equals(str[2]))
										printRowidEqual(file,k,order);
								}
							}
						}
					}
				}
				catch (Exception e) {
					System.out.println("Unable to show the table");
					System.out.println(e);
				}
				break;
			case "insert":
				try{
					tablename=commandTokens[2];
					first=userCommand.indexOf("values");
					last=userCommand.length();
					userCommand=userCommand.substring(first,last);
					first=userCommand.indexOf("(");
					last=userCommand.indexOf(")");
					String content=userCommand.substring(first+1,last).trim();
					String[] values=content.split(",");
					int num=values.length-1;
					String[] column=new String[num];
					for(int i=0;i<num;i++){
						values[i+1]=values[i+1].trim();
						column[i]=values[i+1].trim();
					}
					int rowid=Integer.parseInt(values[0].trim());
					byte[] type=findtype(tablename,num);
					int[] payload=findpayload(column,type);
					int payloads=4;
					for(int i=0;i<num;i++)
						payloads+=payload[i];
					int size=payloads+num+3;
					
					file=new RandomAccessFile("data/davisbase_columns.tbl", "rw");
					int i=0;
					String table_name="";
					while(!table_name.equals("davisbase_"+tablename)){
						i++;
						int page=findpage(file,i,1);
						int no=findNoInLeaf(file,i,page);
						file.seek(512*(page-1)+2*no+2);
						int offset=file.readShort();
						file.seek(offset+7+512*(page-1));
						int length1=file.read()-12;
						file.seek(offset+13+512*(page-1));
						byte[] a=new byte[length1];
						for(int j=0;j<length1;j++)
							a[j]=file.readByte();
						table_name=new String(a);
					}
					i++;
					boolean[] nullable=new boolean[num];
					for(int j=0;j<num;j++){
						int page=findpage(file,i,1);
						int no=findNoInLeaf(file,i,page);
						file.seek(512*(page-1)+2*no+2);
						int offset=file.readShort();
						file.seek(offset+11+512*(page-1));
						int length1=file.read()-12;
						if(length1==3)
							nullable[j]=true;
						else
							nullable[j]=false;
						i++;
					}
					int check=0;
					for(i=0;i<num;i++){
						if(values[i+1].equals("null")&&nullable[i]==false)
							check=1;
					}
					if(check==0){
						file=new RandomAccessFile("data/davisbase_" +tablename+".tbl", "rw");
						int page=findpage(file,rowid,1);
						int abc=findNoInLeaf(file, rowid, page);
						if(abc>0)
							System.out.println("rowid has existed");
						else{
							page=addLeafRow(size,file,page,rowid);
							file.seek(512*(page-1)+1);
							int rows=file.read();
							int offsetstart=file.readShort();
							int no=-1;
							for(i=1;i<=rows;i++){
								file.seek(512*(page-1)+2+2*i);
								int offset=file.readShort();
								file.seek(offset+2+512*(page-1));
								int key=file.readInt();
								if(key>rowid){
									no=i;
									break;
								}
							}
							offsetstart-=size;
							if(no==-1){
								file.seek(512*(page-1)+4+rows*2);
								file.writeShort(offsetstart);
							}
							else{
								for(i=rows;i>no;i--){
									file.seek(512*(page-1)+2+2*i);
									int offset=file.readShort();
									file.writeShort(offset);
								}
							}
							file.seek(512*(page-1)+1);
							file.write(++rows);
							file.writeShort(offsetstart);
							file.seek(offsetstart+512*(page-1));
							file.writeShort(payloads);
							file.writeInt(rowid);
							file.write(num);
							for(i=0;i<num;i++){
								if(type[i]==0x0C)
									file.write(12+column[i].length());
								else
									file.write(type[i]);
							}
							for(i=0;i<num;i++){
								switch(type[i]){
								case 0x00:
									file.write(0);
									break;
								case 0x01:
									file.writeShort(0);
									break;
								case 0x02:
									file.writeInt(0);
									break;
								case 0x03:
									file.writeLong(0);
									break;
								case 0x04:
									file.write(Integer.parseInt(column[i]));
									break;
								case 0x05:
									file.writeShort(Integer.parseInt(column[i]));;
									break;
								case 0x06:
									file.writeInt(Integer.parseInt(column[i]));
									break;
								case 0x07:
									file.writeLong(Integer.parseInt(column[i]));
									break;
								case 0x08:
									file.writeFloat(Float.parseFloat(column[i]));
									break;
								case 0x09:
									file.writeDouble(Double.parseDouble(column[i]));
									break;
								case 0x0A:
									int a=column[i].indexOf("'");
									int b=column[i].lastIndexOf("'");
									column[i]=column[i].substring(a+1, b);
									java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(column[i]);
									long mills = temp.getTime();
									file.writeLong(mills);
									break;
								case 0x0B:
									a=column[i].indexOf("'");
									b=column[i].lastIndexOf("'");
									column[i]=column[i].substring(a+1, b);
									temp = new SimpleDateFormat("yyyy-MM-dd").parse(column[i]);
									mills = temp.getTime();
									file.writeLong(mills);
									break;
								default:
									file.writeBytes(column[i]);
									break;
								}
							}
						}
					}
					else{
						System.out.println("value can not be null");
					}
					
					System.out.println("1 row inserted");
				}
				catch (Exception e) {
					System.out.println("Unable to insert values");
					System.out.println(e);
				}
				break;
			case "update":
				try{
					tablename=commandTokens[1].trim();
					first=userCommand.indexOf("set");
					last=userCommand.indexOf("where");
					String content1=userCommand.substring(first+3,last).trim();
					String[] str1=content1.split("=");
					String column=str1[0].trim();
					String value=str1[1].trim();
					
					first=userCommand.indexOf("where");
					last=userCommand.length();
					String content2=userCommand.substring(first+5,last).trim();
					String[] str2=content2.split("=");
					String f=str2[1].trim();
					int rowid=Integer.parseInt(f);
					file=new RandomAccessFile("data/davisbase_" +tablename+".tbl", "rw");
					int page=findpage(file,rowid,1);
					int no=findNoInLeaf(file,rowid,page);
					file.seek(512*(page-1)+2*no+2);
					int offset=file.readShort();
					file.seek(offset+7+512*(page-1));
					int num=file.read();
					file=new RandomAccessFile("data/davisbase_columns.tbl", "rw");
					String table_name="";
					String column_name="";
					int id=0;
					while(!table_name.equals("davisbase_"+tablename)){
						id++;
						page=findpage(file,id,1);
						no=findNoInLeaf(file,id,page);
						file.seek(512*(page-1)+2*no+2);
						offset=file.readShort();
						file.seek(offset+7+512*(page-1));
						int length1=file.read()-12;
						file.seek(offset+13+512*(page-1));
						byte[] a=new byte[length1];
						for(int j=0;j<length1;j++)
							a[j]=file.readByte();
						table_name=new String(a);
					}
					int j=0;
					while(!column_name.equals(column)){
						j++;
						page=findpage(file,id,1);
						no=findNoInLeaf(file,id,page);
						file.seek(512*(page-1)+2*no+2);
						offset=file.readShort();
						file.seek(offset+7+512*(page-1));
						int length1=file.read()-12;
						file.seek(offset+8+512*(page-1));
						int length2=file.read()-12;
						file.seek(offset+13+length1+512*(page-1));
						byte[] a=new byte[length2];
						for(int k=0;k<length2;k++)
							a[k]=file.readByte();
						column_name=new String(a);
						id++;
					}
					id--;
					j--;

					page=findpage(file,id,1);
					no=findNoInLeaf(file,id,page);
					file.seek((page-1)*512+2+2*no);
					offset=file.readShort();
					file.seek(offset+11+512*(page-1));
					int abc=file.read();
					boolean nullable;
					if(abc==15)
						nullable=true;
					else
						nullable=false;
					if(nullable==false&&value.equals("null")){
						System.out.println("value can not be null");
					}
					else{
						file=new RandomAccessFile("data/davisbase_" +tablename+".tbl", "rw");
						page=findpage(file,rowid,1);
						no=findNoInLeaf(file,rowid,page);
						file.seek(512*(page-1)+2*no+2);
						offset=file.readShort();
						file.seek(offset+6+512*(page-1));
						num=file.read();
						byte[] type=new byte[num];
						int[] payload=new int[num];
						for(int i=0;i<num;i++){
							type[i]=file.readByte();
							switch(type[i]){
							case 0x00:
								payload[i]=1;
								break;
							case 0x01:
								payload[i]=2;
								break;
							case 0x02:
								payload[i]=4;
								break;
							case 0x03:
								payload[i]=8;
								break;
							case 0x04:
								payload[i]=1;
								break;
							case 0x05:
								payload[i]=2;
								break;
							case 0x06:
								payload[i]=4;
								break;
							case 0x07:
								payload[i]=8;
								break;
							case 0x08:
								payload[i]=4;
								break;
							case 0x09:
								payload[i]=8;
								break;
							case 0x0A:
								payload[i]=8;
								break;
							case 0x0B:
								payload[i]=8;
								break;
							default:
								payload[i]=type[i]-12;
								break;
							}

						}
						
						int load=4;
						for(int i=0;i<j-1;i++)
							load=load+payload[i];
						load+=3+num;
						if(type[j-1]>12){
							int length=value.length();
							for(int k=0;k<payload[j-1]-length;k++){
								value=new String(value)+" ";
								System.out.println(payload[j-1]);
							}
						}
						
						
						file.seek(offset+load+512*(page-1));
						switch(type[j-1]){
						case 0x00:
							file.write(0);
							break;
						case 0x01:
							file.writeShort(0);
							break;
						case 0x02:
							file.writeInt(0);
							break;
						case 0x03:
							file.writeLong(0);
							break;
						case 0x04:
							file.write(Integer.parseInt(value));
							break;
						case 0x05:
							file.writeShort(Short.parseShort(value));;
							break;
						case 0x06:
							file.writeInt(Integer.parseInt(value));
							break;
						case 0x07:
							file.writeLong(Long.parseLong(value));
							break;
						case 0x08:
							file.writeFloat(Float.parseFloat(value));
							break;
						case 0x09:
							file.writeDouble(Double.parseDouble(value));
							break;
						case 0x0A:
							java.util.Date temp = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").parse(value);
							long mills = temp.getTime();
							file.writeLong(mills);
							break;
						case 0x0B:
							temp = new SimpleDateFormat("yyyy-MM-dd").parse(value);
							mills = temp.getTime();
							file.writeLong(mills);
							break;
						default:
							file.writeBytes(value);
							break;
						}
						
						
					}
					//System.out.println(x);
;				}
				catch (Exception e) {
					System.out.println("Unable to update values");
					System.out.println(e);
				}
				break;
			case "help":
				help();
				break;
			case "version":
				version();
				break;
			case "exit":
				
				break;
			default:
				System.out.println("I didn't understand the command: \"" + userCommand + "\"");
				break;
		}
	}	
}
