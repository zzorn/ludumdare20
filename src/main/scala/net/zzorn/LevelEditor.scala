package net.zzorn

import java.awt.{BorderLayout, Dimension}
import java.awt.Window._
import javax.swing._
import java.awt.event.{ActionEvent, ActionListener}
import org.scalaprops.{Property, Bean, BeanListener}

/**
 * 
 */
class LevelEditor() {

  private val frame = new JFrame()
  private var level: Level = null
  private val mainPanel = new JPanel(new BorderLayout())
  private var levelEditorUi: JComponent = null
  private val changeListener= new BeanListener {
    def onPropertyRemoved(bean: Bean, property: Property[ _ ]) {}
    def onPropertyAdded(bean: Bean, property: Property[ _ ]) {}
    def onPropertyChanged(bean: Bean, property: Property[ _ ]) {
      reLoadLevel()
    }
  }

  def setLevel(_level: Level) {
    if (level != null) level.removeDeepListener(changeListener)

    level = _level

    mainPanel.remove(levelEditorUi)
    levelEditorUi = makeEditorUi
    mainPanel.add(levelEditorUi, BorderLayout.CENTER)
    mainPanel.invalidate()
    mainPanel.validate()
    mainPanel.repaint()
    frame.pack()

    if (level != null) level.addDeepListener(changeListener)
  }

  def start() {
    levelEditorUi = makeEditorUi

    mainPanel.add(levelEditorUi, BorderLayout.CENTER)

    //mainPanel.add(makeReloadButton, BorderLayout.SOUTH)

    setupFrame(mainPanel)
  }

  private def setupFrame(mainPanel: JComponent) {
    frame.setTitle("Level Editor")
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
    frame.setPreferredSize(new Dimension(300, 500))
    frame.setContentPane(mainPanel)
    frame.pack()
    frame.setVisible(true)
  }

  private def makeReloadButton: JComponent = {
    val button = new JButton("Reload Level")

    button.addActionListener(new ActionListener{
      def actionPerformed(e: ActionEvent) {
        reLoadLevel()
      }
    })
  
    button
  }

  private def reLoadLevel() {
    if (level != null) Ludum20.loadLevel(level)
  }

  private def makeEditorUi: JComponent = {
    if (level != null) level.createEditor
    else new JPanel()
  }

}