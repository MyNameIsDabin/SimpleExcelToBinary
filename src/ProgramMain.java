import java.awt.EventQueue;

public class ProgramMain
{
	public static SimpleETB window;
	
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					window = new SimpleETB();
					window.getFrame().setTitle(APP_INFO.TITLE + " " + APP_INFO.VERSION);
					window.getFrame().setVisible(true);
					window.getFrame().setResizable(false);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
}
