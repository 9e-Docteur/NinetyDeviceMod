package com.mrcrayfish.device.api.app;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mrcrayfish.device.api.app.Layout.Background;
import com.mrcrayfish.device.api.app.component.Button;
import com.mrcrayfish.device.api.app.component.Image;
import com.mrcrayfish.device.api.app.component.*;
import com.mrcrayfish.device.api.app.component.Label;
import com.mrcrayfish.device.api.app.component.TextField;
import com.mrcrayfish.device.api.app.listener.ClickListener;
import com.mrcrayfish.device.api.app.renderer.ListItemRenderer;
import com.mrcrayfish.device.api.io.File;
import com.mrcrayfish.device.api.print.IPrint;
import com.mrcrayfish.device.api.task.Task;
import com.mrcrayfish.device.api.task.TaskManager;
import com.mrcrayfish.device.api.utils.RenderUtil;
import com.mrcrayfish.device.core.Laptop;
import com.mrcrayfish.device.core.Wrappable;
import com.mrcrayfish.device.core.io.FileSystem;
import com.mrcrayfish.device.core.network.NetworkDevice;
import com.mrcrayfish.device.core.network.task.TaskGetDevices;
import com.mrcrayfish.device.core.print.task.TaskPrint;
import com.mrcrayfish.device.programs.system.component.FileBrowser;
import com.mrcrayfish.device.programs.system.object.ColorScheme;
import com.mrcrayfish.device.tileentity.TileEntityPrinter;
import com.mrcrayfish.device.util.GLHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.function.Predicate;

public abstract class Dialog extends Wrappable
{
	private String title = "Message";
	private int width;
	private int height;

	protected final Layout defaultLayout;
	private Layout customLayout;
	
	private boolean pendingLayoutUpdate = true;
	private boolean pendingClose = false;

	public Dialog() 
	{
		this.defaultLayout = new Layout(150, 40);
	}
	
	protected final void addComponent(Component c)
	{
		if (c != null)
		{
			defaultLayout.addComponent(c);
			c.init(defaultLayout);
		}
	}
	
	protected final void setLayout(Layout layout)
	{
		this.customLayout = layout;
		this.width = layout.width;
		this.height = layout.height;
		this.pendingLayoutUpdate = true;
		this.customLayout.handleLoad();
	}

	@Override
	public void init(@Nullable CompoundTag intent)
	{
		this.defaultLayout.clear();
		this.setLayout(defaultLayout);
	}

	@Override
	public void onTick()
	{
		if(pendingClose && getWindow().getDialogWindow() == null)
		{
			getWindow().close();
		}
		customLayout.handleTick();
	}

	@Override
	public void render(Laptop laptop, Minecraft mc, int x, int y, int mouseX, int mouseY, boolean active, float partialTicks)
	{
		PoseStack poseStack = new PoseStack();
		GL11.glEnable(GL11.GL_SCISSOR_TEST);
		
		GLHelper.pushScissor(x, y, width, height);
		customLayout.render(poseStack, laptop, mc, x, y, mouseX, mouseY, active, partialTicks);
		GLHelper.popScissor();

		GL11.glDisable(GL11.GL_SCISSOR_TEST);

		customLayout.renderOverlay(laptop, mc, mouseX, mouseY, active);

		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		//RenderHelper.disableStandardItemLighting();
	}

	@Override
	public void handleMouseClick(int mouseX, int mouseY, int mouseButton)
	{
		customLayout.handleMouseClick(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseDrag(int mouseX, int mouseY, int mouseButton)
	{
		customLayout.handleMouseDrag(mouseX, mouseY, mouseButton);
	}

	@Override
	public void handleMouseRelease(int mouseX, int mouseY, int mouseButton)
	{
		customLayout.handleMouseRelease(mouseX, mouseY, mouseButton);
	}
	
	@Override
	public void handleMouseScroll(int mouseX, int mouseY, boolean direction)
	{
		customLayout.handleMouseScroll(mouseX, mouseY, direction);
	}

	@Override
	public void handleKeyTyped(char character, int code)
	{
		customLayout.handleKeyTyped(character, code);
	}
	
	@Override
	public void handleKeyReleased(char character, int code)
	{
		customLayout.handleKeyReleased(character, code);
	}
	
	public void setTitle(String title)
	{
		this.title = title;
	}

	@Override
	public String getWindowTitle()
	{
		return title;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public int getHeight()
	{
		return height;
	}
	
	@Override
	public void markForLayoutUpdate()
	{
		this.pendingLayoutUpdate = true;
	}

	@Override
	public boolean isPendingLayoutUpdate()
	{
		return pendingLayoutUpdate;
	}

	@Override
	public void clearPendingLayout()
	{
		this.pendingLayoutUpdate = false;
	}

	@Override
	public void updateComponents(int x, int y)
	{
		customLayout.updateComponents(x, y);
	}

	@Override
	public void onClose() {}

	public void close()
	{
		this.pendingClose = true;
	}

	public static class Message extends Dialog
	{
		private String messageText = "";
		
		private ClickListener positiveListener;
		private Button buttonPositive;
		
		public Message(String messageText)
		{
			this.messageText = messageText;
		}
		
		@Override
		public void init(@Nullable CompoundTag intent)
		{
			super.init(intent);
			
			int lines = Minecraft.getInstance().font.wordWrapHeight(messageText, getWidth() - 10);
			defaultLayout.height += (lines - 1) * 9;
			
			super.init(intent);
			
			defaultLayout.setBackground(new Background()
			{
				@Override
				public void render(PoseStack poseStack, Screen gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
				{
					Gui.fill(poseStack, x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
				}
			});
			
			Text message = new Text(messageText, 5, 5, getWidth() - 10);
			this.addComponent(message);
			
			buttonPositive = new Button(getWidth() - 41, getHeight() - 20, "Close");
			buttonPositive.setSize(36, 16);
			buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
			{
                if(positiveListener != null)
                {
                    positiveListener.onClick(mouseX, mouseY, mouseButton);
                }
                close();
            });
			this.addComponent(buttonPositive);
		}
	}

	/**
	 * A simple confirmation dialog
	 *
	 * This can be used to prompt as user to confirm whether a
	 * task should run. For instance, the FileBrowser component
	 * uses this dialog to prompt the user if it should override
	 * a file.
	 */
	public static class Confirmation extends Dialog
	{
		private static final int DIVIDE_WIDTH = 15;

		private String messageText = "Are you sure?";
		private String positiveText = "Yes";
		private String negativeText = "No";
		
		private ClickListener positiveListener;
		private ClickListener negativeListener;
		
		private Button buttonPositive;
		private Button buttonNegative;

		public Confirmation() {}

		public Confirmation(String messageText)
		{
			this.messageText = messageText;
		}

		@Override
		public void init(@Nullable CompoundTag intent)
		{
			super.init(intent);
			
			int lines = Minecraft.getInstance().font.wordWrapHeight(messageText, getWidth() - 10);
			defaultLayout.height += (lines - 1) * 9;
			
			super.init(intent);

			defaultLayout.setBackground(new Background()
			{
				@Override
				public void render(PoseStack poseStack, Screen gui, Minecraft mc, int x, int y, int width, int height, int mouseX, int mouseY, boolean windowActive)
				{
					Gui.fill(poseStack, x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
				}
			});
			
			Text message = new Text(messageText, 5, 5, getWidth() - 10);
			this.addComponent(message);

			int positiveWidth = Minecraft.getInstance().font.width(positiveText);
			buttonPositive = new Button(getWidth() - positiveWidth - DIVIDE_WIDTH, getHeight() - 20, positiveText);
			buttonPositive.setSize(positiveWidth + 10, 16);
			buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
			{
				if(positiveListener != null)
				{
					positiveListener.onClick(mouseX, mouseY, mouseButton);
				}
				close();
			});
			this.addComponent(buttonPositive);

			int negativeWidth = Math.max(20, Minecraft.getInstance().font.width(negativeText));
			buttonNegative = new Button(getWidth() - DIVIDE_WIDTH - positiveWidth - DIVIDE_WIDTH - negativeWidth + 1, getHeight() - 20, negativeText);
			buttonNegative.setSize(negativeWidth + 10, 16);
			buttonNegative.setClickListener((mouseX, mouseY, mouseButton) ->
			{
				if(negativeListener != null)
				{
					negativeListener.onClick(mouseX, mouseY, mouseButton);
				}
				close();
			});
			this.addComponent(buttonNegative);
		}

		/**
		 * Sets the positive button text
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText)
		{
			if(positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText)
		{
			if(negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}
		
		public void setPositiveListener(ClickListener positiveListener)
		{
			this.positiveListener = positiveListener;
		}
		
		public void setNegativeListener(ClickListener negativeListener)
		{
			this.negativeListener = negativeListener;
		}
		
		public void setMessageText(String messageText)
		{
			this.messageText = messageText;
		}
	}

	/**
	 * A simple dialog to retrieve text input from the user
	 */
	public static class Input extends Dialog
	{
		private static final int DIVIDE_WIDTH = 15;

		private String messageText = null;
		private String inputText = "";
		private String positiveText = "Okay";
		private String negativeText = "Cancel";

		private ResponseHandler<String> responseListener;

		private TextField textFieldInput;
		private Button buttonPositive;
		private Button buttonNegative;

		public Input() {}

		public Input(String messageText)
		{
			this.messageText = messageText;
		}

		@Override
		public void init(@Nullable CompoundTag intent)
		{
			super.init(intent);

			int offset = 0;

			if(messageText != null)
			{
				int lines = Minecraft.getInstance().font.wordWrapHeight(messageText, getWidth() - 10);
				defaultLayout.height += lines * 9 + 10;
				offset += lines * 9 + 5;
			}

			super.init(intent);

			defaultLayout.setBackground((poseStack, gui, mc, x, y, width, height, mouseX, mouseY, windowActive) -> {
				Gui.fill(poseStack, x, y, x + width, y + height, Color.LIGHT_GRAY.getRGB());
			});

			if(messageText != null)
			{
				Text message = new Text(messageText, 5, 5, getWidth() - 10);
				this.addComponent(message);
			}

			textFieldInput = new TextField(5, 5 + offset, getWidth() - 10);
			textFieldInput.setText(inputText);
			textFieldInput.setFocused(true);
			this.addComponent(textFieldInput);

			int positiveWidth = Minecraft.getInstance().font.width(positiveText);
			buttonPositive = new Button(getWidth() - positiveWidth - DIVIDE_WIDTH, getHeight() - 20, positiveText);
			buttonPositive.setSize(positiveWidth + 10, 16);
			buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
			{
                if(!textFieldInput.getText().isEmpty())
                {
                	boolean close = true;
                    if(responseListener != null)
                    {
                        close = responseListener.onResponse(true, textFieldInput.getText().trim());
                    }
					if(close) close();
                }
            });
			this.addComponent(buttonPositive);

			int negativeWidth = Minecraft.getInstance().font.width(negativeText);
			buttonNegative = new Button(getWidth() - DIVIDE_WIDTH - positiveWidth - DIVIDE_WIDTH - negativeWidth + 1, getHeight() - 20, negativeText);
			buttonNegative.setSize(negativeWidth + 10, 16);
			buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
			this.addComponent(buttonNegative);
		}

		/**
		 * Sets the initial text for the input text field
		 * @param inputText
		 */
		public void setInputText(@Nonnull String inputText)
		{
			if(inputText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.inputText = inputText;
		}

		/**
		 * Gets the input text field. This will be null if has not been
		 * @return
		 */
		@Nullable
		public TextField getTextFieldInput()
		{
			return textFieldInput;
		}

		/**
		 * Sets the positive button text
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText)
		{
			if(positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText)
		{
			if(negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		/**
		 * Sets the response handler. The handler is called when the positive
		 * button is pressed and returns the value in the input text field. Returning
		 * true in the handler indicates that the dialog should close.
		 *
		 * @param responseListener
		 */
		public void setResponseHandler(ResponseHandler<String> responseListener)
		{
			this.responseListener = responseListener;
		}
	}

	public static class OpenFile extends Dialog
	{
		private final Application app;

		private String positiveText = "Open";
		private String negativeText = "Cancel";

		private Layout main;
		private FileBrowser browser;
		private Button buttonPositive;
		private Button buttonNegative;

		private ResponseHandler<File> responseListener;
		private Predicate<File> filter;

		public OpenFile(Application app)
		{
			this.app = app;
			this.setTitle("Open File");
		}

		@Override
		public void init(@Nullable CompoundTag intent)
		{
			super.init(intent);

			main = new Layout(211, 126);

			browser = new FileBrowser(0, 0, app, FileBrowser.Mode.BASIC);
			browser.openFolder(FileSystem.DIR_HOME);
			browser.setFilter(file -> filter == null || filter.test(file) || file.isFolder());
			browser.setItemClickListener((file, index, mouseButton) ->
			{
				if(mouseButton == 0)
				{
					if(!file.isFolder())
					{
						buttonPositive.setEnabled(true);
					}
				}
			});
			main.addComponent(browser);

			int positiveWidth = Minecraft.getInstance().font.width(positiveText);
			buttonPositive = new Button(172, 106, positiveText);
			buttonPositive.setSize(positiveWidth + 10, 16);
			buttonPositive.setEnabled(false);
			buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
			{
				if(mouseButton == 0)
				{
					File file = browser.getSelectedFile();
					if(file != null)
					{
						boolean close = true;
						if (responseListener != null)
						{
							close = responseListener.onResponse(true, file);
						}
						if (close) close();
					}
				}
			});
			main.addComponent(buttonPositive);

			int negativeWidth = Minecraft.getInstance().font.width(negativeText);
			buttonNegative = new Button(125, 106, negativeText);
			buttonNegative.setSize(negativeWidth + 10, 16);
			buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
			main.addComponent(buttonNegative);

			this.setLayout(main);
		}

		/**
		 * Sets the positive button text
		 * @param positiveText
		 */
		public void setPositiveText(String positiveText)
		{
			if(positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(String negativeText)
		{
			if(negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		/**
		 * Sets the response handler. The handler is called when the positive
		 * button is pressed and returns the file that is selected. Returning
		 * true in the handler indicates that the dialog should close.
		 *
		 * @param responseListener the response handler to handle the returned file
		 */
		public void setResponseHandler(ResponseHandler<File> responseListener)
		{
			this.responseListener = responseListener;
		}

		/**
		 * Sets the filter for the file list to show only files that match certain conditions.
		 *
		 * @param filter the predicate
		 */
		public void setFilter(Predicate<File> filter)
		{
			this.filter = filter;
		}

		/**
		 * Sets the filter for the file list to show only files that can open with the specified
		 * application.
		 *
		 * @param app the predicate
		 */
		public void setFilter(Application app)
		{
			this.filter = file -> app.getInfo().getFormattedId().equals(file.getOpeningApp());
		}
	}

	public static class SaveFile extends Dialog
	{
		private final Application app;
		private String name;
		private final CompoundTag data;

		private String positiveText = "Save";
		private String negativeText = "Cancel";

		private Layout main;
		private FileBrowser browser;
		private TextField textFieldFileName;
		private Button buttonPositive;
		private Button buttonNegative;

		public ResponseHandler<File> responseHandler;
		private Predicate<File> filter;

		private String path = FileSystem.DIR_HOME;

		public SaveFile(Application app, CompoundTag data)
		{
			this.app = app;
			this.data = data;
			this.setTitle("Save File");
		}

		public SaveFile(Application app, File file)
		{
			this.app = app;
			this.name = file.getName();
			this.data = file.toTag();
			this.setTitle("Save File");
		}

		@Override
		public void init(@Nullable CompoundTag intent)
		{
			super.init(intent);
			main = new Layout(211, 145);

			browser = new FileBrowser(0, 0, app, FileBrowser.Mode.BASIC);
			browser.setFilter(file -> filter == null || filter.test(file) || file.isFolder());
			browser.openFolder(path);
			main.addComponent(browser);

			buttonPositive = new Button(172, 125, positiveText);
			buttonPositive.setClickListener((mouseX, mouseY, mouseButton) ->
			{
				if(mouseButton == 0)
				{
					if(!textFieldFileName.getText().isEmpty())
					{
						if(!FileSystem.PATTERN_FILE_NAME.matcher(textFieldFileName.getText()).matches())
						{
							Dialog.Message dialog = new Dialog.Message("File name may only contain letters, numbers, underscores and spaces.");
							app.openDialog(dialog);
							return;
						}

						File file;
						if(name != null)
						{
							file = File.fromTag(textFieldFileName.getText(), data);
						}
						else
						{
							file = new File(textFieldFileName.getText(), app, data.copy());
						}

						browser.addFile(file, (response, success) ->
						{
							if(response.getStatus() == FileSystem.Status.FILE_EXISTS)
							{
								Dialog.Confirmation dialog = new Dialog.Confirmation("A file with that name already exists. Are you sure you want to override it?");
								dialog.setPositiveText("Override");
								dialog.setPositiveListener((mouseX1, mouseY1, mouseButton1) ->
								{
									browser.addFile(file, true, (response1, success1) ->
									{
                                        dialog.close();

                                        //TODO Look into better handling. Get response from parent if should close. Maybe a response interface w/ generic
                                        if(responseHandler != null)
                                        {
                                            responseHandler.onResponse(success1, file);
                                        }
                                        SaveFile.this.close();
                                    });
								});
								app.openDialog(dialog);
							}
							else
							{
								if(responseHandler != null)
								{
									responseHandler.onResponse(true, file);
								}
								close();
							}
                        });
					}
				}
			});
			main.addComponent(buttonPositive);

			buttonNegative = new Button(126, 125, negativeText);
			buttonNegative.setClickListener((mouseX, mouseY, mouseButton) -> close());
			main.addComponent(buttonNegative);

			textFieldFileName = new TextField(26, 105, 180);
			textFieldFileName.setFocused(true);
			if(name != null) textFieldFileName.setText(name);
			main.addComponent(textFieldFileName);

			this.setLayout(main);
		}

		/**
		 * Sets the positive button text
		 * @param positiveText
		 */
		public void setPositiveText(@Nonnull String positiveText)
		{
			if(positiveText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.positiveText = positiveText;
		}

		/**
		 * Sets the negative button text
		 *
		 * @param negativeText
		 */
		public void setNegativeText(@Nonnull String negativeText)
		{
			if(negativeText == null) {
				throw new IllegalArgumentException("Text can't be null");
			}
			this.negativeText = negativeText;
		}

		/**
		 * Sets the response handler. The handler is called when the positive
		 * button is pressed and returns the file that is selected. Returning
		 * true in the handler indicates that the dialog should close.
		 *
		 * @param responseHandler the response handler to handle the returned file
		 */
		public void setResponseHandler(ResponseHandler<File> responseHandler)
		{
			this.responseHandler = responseHandler;
		}

		/**
		 * Sets the filter for the file list to show only files that match certain conditions.
		 *
		 * @param filter the predicate
		 */
		public void setFilter(Predicate<File> filter)
		{
			this.filter = filter;
		}

		/**
		 * Sets the filter for the file list to show only files that can open with the specified
		 * application.
		 *
		 * @param app the predicate
		 */
		public void setFilter(Application app)
		{
			this.filter = file -> app.getInfo().getFormattedId().equals(file.getOpeningApp());
		}

		/**
		 * Sets the initial folder path to be shown when the the dialog is opened
		 *
		 * @param path the initial folder path
		 */
		public void setFolder(String path)
		{
			this.path = path;
		}
	}

	public static class Print extends Dialog
	{
		private final IPrint print;

		private Layout layoutMain;
		private Label labelMessage;
		private Button buttonRefresh;
		private ItemList<NetworkDevice> itemListPrinters;
		private Button buttonPrint;
		private Button buttonCancel;
		private Button buttonInfo;

		public Print(IPrint print)
		{
			this.print = print;
			this.setTitle("Print");
		}

		@Override
		public void init(@Nullable CompoundTag intent)
		{
			super.init(intent);

			layoutMain = new Layout(150, 132);

			labelMessage = new Label("Select a Printer", 5, 5);
			layoutMain.addComponent(labelMessage);

			buttonRefresh = new Button(131, 2, Icons.RELOAD);
			buttonRefresh.setPadding(2);
			buttonRefresh.setToolTip("Refresh", "Retrieve an updated list of printers");
			buttonRefresh.setClickListener((mouseX, mouseY, mouseButton) ->
			{
                if(mouseButton == 0)
				{
					itemListPrinters.setSelectedIndex(-1);
					getPrinters(itemListPrinters);
				}
            });
			layoutMain.addComponent(buttonRefresh);

			itemListPrinters = new ItemList<>(5, 18, 140, 5);
			itemListPrinters.setListItemRenderer(new ListItemRenderer<NetworkDevice>(16)
			{
				@Override
				public void render(PoseStack poseStack, NetworkDevice networkDevice, Screen gui, Minecraft mc, int x, int y, int width, int height, boolean selected)
				{
					ColorScheme colorScheme = Laptop.getSystem().getSettings().getColorScheme();
					Gui.fill(poseStack, x, y, x + width, y + height, selected ? colorScheme.getItemHighlightColor() : colorScheme.getItemBackgroundColor());
					Icons.PRINTER.draw(poseStack, mc, x + 3, y + 3);
					RenderUtil.drawStringClipped(poseStack, networkDevice.getName(), x + 18, y + 4, 118, Laptop.getSystem().getSettings().getColorScheme().getTextColor(), true);
				}
			});
			itemListPrinters.setItemClickListener((blockPos, index, mouseButton) ->
			{
                if(mouseButton == 0)
				{
					buttonPrint.setEnabled(true);
					buttonInfo.setEnabled(true);
				}
            });
			itemListPrinters.sortBy((o1, o2) ->
			{
				BlockPos laptopPos = Laptop.getPos();

				BlockPos pos1 = o1.getPos();
				double distance1 = laptopPos.distToCenterSqr(pos1.getX() + 0.5, pos1.getY() + 0.5, pos1.getZ() + 0.5);

				BlockPos pos2 = o2.getPos();
				double distance2 = laptopPos.distToCenterSqr(pos2.getX() + 0.5, pos2.getY() + 0.5, pos2.getZ() + 0.5);

				return distance2 < distance1 ? 1 : (distance1 == distance2) ? 0 : -1;
			});
			layoutMain.addComponent(itemListPrinters);

			buttonPrint = new Button(98, 108, "Print", Icons.CHECK);
			buttonPrint.setPadding(5);
			buttonPrint.setEnabled(false);
			buttonPrint.setClickListener((mouseX, mouseY, mouseButton) ->
			{
				if(mouseButton == 0)
				{
					NetworkDevice networkDevice = itemListPrinters.getSelectedItem();
					if(networkDevice != null)
					{
						TaskPrint task = new TaskPrint(Laptop.getPos(), networkDevice, print);
						task.setCallback((CompoundTag, success) ->
						{
							if(success)
							{
								close();
							}
						});
						TaskManager.sendTask(task);
					}
				}
			});
			layoutMain.addComponent(buttonPrint);

			buttonCancel = new Button(74, 108, Icons.CROSS);
			buttonCancel.setPadding(5);
			buttonCancel.setClickListener((mouseX, mouseY, mouseButton) ->
			{
				if(mouseButton == 0)
				{
					close();
				}
			});
			layoutMain.addComponent(buttonCancel);

			buttonInfo = new Button(5, 108, Icons.HELP);
			buttonInfo.setEnabled(false);
			buttonInfo.setPadding(5);
			buttonInfo.setClickListener((mouseX, mouseY, mouseButton) ->
			{
                if(mouseButton == 0)
				{
					NetworkDevice printerEntry = itemListPrinters.getSelectedItem();
					if(printerEntry != null)
					{
						Info info = new Info(printerEntry);
						openDialog(info);
					}
				}
            });
			layoutMain.addComponent(buttonInfo);

			setLayout(layoutMain);

			getPrinters(itemListPrinters);
		}

		private void getPrinters(ItemList<NetworkDevice> itemList)
		{
			itemList.removeAll();
			itemList.setLoading(true);
			Task task = new TaskGetDevices(Laptop.getPos(), TileEntityPrinter.class);
			task.setCallback((tagCompound, success) ->
			{
				if(success)
				{
					ListTag tagList = tagCompound.getList("network_devices", Tag.TAG_COMPOUND);
					for(int i = 0; i < tagList.size(); i++)
					{
						itemList.addItem(NetworkDevice.fromTag(tagList.getCompound(i)));
					}
					itemList.setLoading(false);
				}
			});
			TaskManager.sendTask(task);
		}

		private static class Info extends Dialog
		{
			private final NetworkDevice entry;

			private Layout layoutMain;
			private Label labelName;
			private Image imagePaper;
			private Label labelPaper;
			private Label labelPosition;
			private Button buttonClose;

			private Info(NetworkDevice entry)
			{
				this.entry = entry;
				this.setTitle("Details");
			}

			@Override
			public void init(@Nullable CompoundTag intent)
			{
				super.init(intent);

				layoutMain = new Layout(120, 70);

				labelName = new Label(ChatFormatting.GOLD.toString() + ChatFormatting.BOLD + entry.getName(), 5, 5);
				layoutMain.addComponent(labelName);

				labelPaper = new Label(ChatFormatting.DARK_GRAY + "Paper: " + ChatFormatting.RESET + 0, 5, 18); //TODO fix paper count
				labelPaper.setAlignment(Component.ALIGN_LEFT);
				labelPaper.setShadow(false);
				layoutMain.addComponent(labelPaper);

				String position = ChatFormatting.DARK_GRAY + "X: " + ChatFormatting.RESET + entry.getPos().getX() + " " + ChatFormatting.DARK_GRAY + "Y: " + ChatFormatting.RESET + entry.getPos().getY() + " " + ChatFormatting.DARK_GRAY + "Z: " + ChatFormatting.RESET + entry.getPos().getZ();
				labelPosition = new Label(position, 5, 30);
				labelPosition.setShadow(false);
				layoutMain.addComponent(labelPosition);

				buttonClose = new Button(5, 49, "Close");
				buttonClose.setClickListener((mouseX, mouseY, mouseButton) ->
				{
                    if(mouseButton == 0)
					{
						close();
					}
                });
				layoutMain.addComponent(buttonClose);

				setLayout(layoutMain);
			}
		}
	}

	/**
	 * The response listener interface. Used for handling responses
	 * from components. The generic is the returned value.
	 *
	 * @author MrCrayfish
	 */
	public interface ResponseHandler<E>
	{
		/**
		 * Called when a response is thrown.
		 *
		 * @param success if the executing task was successful
		 */
		boolean onResponse(boolean success, E e);
	}

}
