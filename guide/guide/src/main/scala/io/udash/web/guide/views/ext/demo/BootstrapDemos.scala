package io.udash.web.guide.views.ext.demo

import java.util.concurrent.TimeUnit

import io.udash._
import io.udash.bindings.modifiers.Binding
import io.udash.bootstrap._
import io.udash.bootstrap.alert.{DismissibleUdashAlert, UdashAlert}
import io.udash.bootstrap.badge.UdashBadge
import io.udash.bootstrap.breadcrumb.UdashBreadcrumbs
import io.udash.bootstrap.button._
import io.udash.bootstrap.card.UdashCard
import io.udash.bootstrap.collapse.{UdashAccordion, UdashCollapse}
import io.udash.bootstrap.datepicker.UdashDatePicker
import io.udash.bootstrap.dropdown.UdashDropdown
import io.udash.bootstrap.dropdown.UdashDropdown.{DefaultDropdownItem, DropdownEvent}
import io.udash.bootstrap.form.{UdashForm, UdashInputGroup}
import io.udash.bootstrap.jumbotron.UdashJumbotron
import io.udash.bootstrap.list.UdashListGroup
import io.udash.bootstrap.modal.UdashModal
import io.udash.bootstrap.nav.{UdashNav, UdashNavbar}
import io.udash.bootstrap.pagination.UdashPagination
import io.udash.bootstrap.progressbar.UdashProgressBar
import io.udash.bootstrap.table.UdashTable
import io.udash.bootstrap.tooltip.{UdashPopover, UdashTooltip}
import io.udash.bootstrap.utils.BootstrapStyles.{Color, ResponsiveBreakpoint, Size}
import io.udash.bootstrap.utils._
import io.udash.css.CssView
import io.udash.logging.CrossLogging
import io.udash.properties.seq.SeqProperty
import io.udash.web.commons.styles.GlobalStyles
import io.udash.web.guide.components.{MenuContainer, MenuEntry, MenuLink}
import io.udash.web.guide.styles.partials.GuideStyles
import io.udash.web.guide.{BootstrapExtState, IntroState}
import org.scalajs.dom
import scalatags.JsDom

import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.Random

object BootstrapDemos extends CrossLogging with CssView {

  import JsDom.all._
  import io.udash.web.guide.Context._
  import org.scalajs.dom._

  def statics(): dom.Element =
    div(BootstrapStyles.Grid.row, GuideStyles.frame)(
      div(BootstrapStyles.Grid.col(9), BootstrapStyles.Border.border())(
        ".col-xs-9"
      ),
      div(BootstrapStyles.Grid.col(4), BootstrapStyles.Border.border())(
        ".col-xs-4", br,
        "Since 9 + 4 = 13 > 12, this 4-column-wide div",
        "gets wrapped onto a new line as one contiguous unit."
      ),
      div(BootstrapStyles.Grid.col(6), BootstrapStyles.Border.border())(
        ".col-xs-6", br,
        "Subsequent columns continue along the new line."
      )
    ).render

  def icons(): dom.Element =
    div(GuideStyles.frame)(
      UdashButtonToolbar()(
        UdashButtonGroup()(
          UdashButton()(i(UdashIcons.FontAwesome.Solid.alignLeft)).render,
          UdashButton()(i(UdashIcons.FontAwesome.Solid.alignCenter)).render,
          UdashButton()(i(UdashIcons.FontAwesome.Solid.alignRight)).render,
          UdashButton()(i(UdashIcons.FontAwesome.Solid.alignJustify)).render
        ).render,
        UdashButtonGroup()(
          UdashButton()(i(UdashIcons.FontAwesome.Brands.bitcoin)).render,
          UdashButton()(i(UdashIcons.FontAwesome.Solid.euroSign)).render,
          UdashButton()(i(UdashIcons.FontAwesome.Solid.dollarSign)).render,
          UdashButton()(i(UdashIcons.FontAwesome.Brands.superpowers)).render,
        ).render
      ).render
    ).render

  def datePicker(): dom.Element = {
    import java.{util => ju}
    val date = Property[Option[ju.Date]](Some(new ju.Date()))

    val pickerOptions = ModelProperty(new UdashDatePicker.DatePickerOptions(
      format = "MMMM Do YYYY, hh:mm a",
      locale = Some("en_GB"),
      showClear = true
    ))

    val disableWeekends = Property(false)
    disableWeekends.streamTo(pickerOptions.subSeq(_.daysOfWeekDisabled)) {
      case true => Seq(UdashDatePicker.DayOfWeek.Saturday, UdashDatePicker.DayOfWeek.Sunday)
      case false => Seq.empty
    }

    val picker: UdashDatePicker = UdashDatePicker(date, pickerOptions)()

    val showButton = UdashButton()("Show")
    val hideButton = UdashButton()("Hide")
    val enableButton = UdashButton()("Enable")
    val disableButton = UdashButton()("Disable")
    showButton.listen { case _ => picker.show() }
    hideButton.listen { case _ => picker.hide() }
    enableButton.listen { case _ => picker.enable() }
    disableButton.listen { case _ => picker.disable() }

    val events = SeqProperty[String](Seq.empty)
    picker.listen {
      case UdashDatePicker.DatePickerEvent.Show(_) => events.append("Widget shown")
      case UdashDatePicker.DatePickerEvent.Hide(_, date) => events.append(s"Widget hidden with date: $date")
      case UdashDatePicker.DatePickerEvent.Change(_, date, oldDate) => events.append(s"Widget change from $oldDate to $date")
    }

    div(GuideStyles.frame)(
      UdashDatePicker.loadBootstrapDatePickerStyles(),
      UdashInputGroup()(
        UdashInputGroup.input(picker.render),
        UdashInputGroup.appendText(bind(date.transform(_.toString)))
      ).render,
      hr,
      UdashForm() { factory => Seq[Modifier](
        factory.input.formGroup()(
          input = _ => factory.input.textInput(pickerOptions.subProp(_.format))().render,
          labelContent = Some(_ => "Date format")
        ),
        factory.input.formGroup()(
          input = _ => factory.input.select(
            pickerOptions.subProp(_.locale).transform[String]((_: Option[String]).get, Some(_: String)),
            Seq("en_GB", "pl", "ru", "af").toSeqProperty
          )(span(_)).render,
          labelContent = Some(_ => "Locale")
        ),
        factory.input.checkbox(disableWeekends)(
          labelContent = Some(_ => "Disable weekends")
        ),
        factory.input.checkbox(pickerOptions.subProp(_.showTodayButton))(
          labelContent = Some(_ => "Show `today` button")
        ),
        factory.input.checkbox(pickerOptions.subProp(_.showClose))(
          labelContent = Some(_ => "Show `close` button")
        ),
        UdashButtonGroup()(
          factory.externalBinding(showButton).render,
          factory.externalBinding(hideButton).render,
            factory.externalBinding(enableButton).render,
              factory.externalBinding(disableButton).render
        ).render
      )}.render,
      hr,
      div(BootstrapStyles.Border.border())(
        repeat(events)(ev => Seq(i(ev.get).render, br.render))
      )
    ).render
  }

  def datePickerRange(): dom.Element = {
    import java.{util => ju}
    val now = new ju.Date().getTime
    val sevenDays = TimeUnit.DAYS.toMillis(7)
    val from = Property[Option[ju.Date]](Some(new ju.Date(now - sevenDays)))
    val to = Property[Option[ju.Date]](Some(new ju.Date(now + sevenDays)))

    val fromPickerOptions = ModelProperty(new UdashDatePicker.DatePickerOptions(
      format = "MMMM Do YYYY",
      locale = Some("en_GB")
    ))

    val toPickerOptions = ModelProperty(new UdashDatePicker.DatePickerOptions(
      format = "D MMMM YYYY",
      locale = Some("pl")
    ))

    val fromPicker: UdashDatePicker = UdashDatePicker(from, fromPickerOptions)()
    val toPicker: UdashDatePicker = UdashDatePicker(to, toPickerOptions)()

    UdashDatePicker.dateRange(fromPicker, toPicker)(fromPickerOptions, toPickerOptions)

    div(GuideStyles.frame)(
      UdashDatePicker.loadBootstrapDatePickerStyles(),
      UdashInputGroup()(
        UdashInputGroup.prependText("From"),
        UdashInputGroup.input(fromPicker.render),
        UdashInputGroup.appendText("to"),
        UdashInputGroup.input(toPicker.render)
      ).render
    ).render
  }

  def tables(): dom.Element = {
    val responsive = Property[Option[ResponsiveBreakpoint]](Some(ResponsiveBreakpoint.All))
    val dark = Property(false)
    val striped = Property(true)
    val bordered = Property(true)
    val hover = Property(true)
    val small = Property(false)

    val darkButton = UdashButton.toggle(active = dark)("Dark theme")
    val stripedButton = UdashButton.toggle(active = striped)("Striped")
    val borderedButton = UdashButton.toggle(active = bordered)("Bordered")
    val hoverButton = UdashButton.toggle(active = hover)("Hover")
    val smallButton = UdashButton.toggle(active = small)("Small")

    val items = SeqProperty(
      Seq.fill(7)((Random.nextDouble(), Random.nextDouble(), Random.nextDouble()))
    )
    val table = UdashTable(
      items, responsive, dark,
      striped = striped,
      bordered = bordered,
      hover = hover,
      small = small
    )(
      headerFactory = Some(_ => tr(th(b("x")), th(b("y")), th(b("z"))).render),
      rowFactory = (el, nested) => tr(
        td(nested(produce(el)(v => span(v._1).render))),
        td(nested(produce(el)(v => span(v._2).render))),
        td(nested(produce(el)(v => span(v._3).render)))
      ).render
    )

    div(GuideStyles.frame)(
      UdashButtonGroup(justified = true.toProperty)(
        darkButton.render,
        stripedButton.render,
        borderedButton.render,
        hoverButton.render,
        smallButton.render
      ).render,
      table.render
    ).render
  }

  def dropdown(): dom.Element = {
    val url = Url(BootstrapExtState.url)
    val items = SeqProperty[UdashDropdown.DefaultDropdownItem](Seq(
      UdashDropdown.DefaultDropdownItem.Header("Start"),
      UdashDropdown.DefaultDropdownItem.Link("Intro", Url(IntroState.url)),
      UdashDropdown.DefaultDropdownItem.Disabled(UdashDropdown.DefaultDropdownItem.Link("Test Disabled", url)),
      UdashDropdown.DefaultDropdownItem.Divider,
      UdashDropdown.DefaultDropdownItem.Header("Dynamic")
    ))

    val clicks = SeqProperty[String](Seq.empty)
    var i = 1
    val appendHandler = window.setInterval(() => {
      items.append(UdashDropdown.DefaultDropdownItem.Link(s"Test $i", url))
      i += 1
    }, 5000)
    window.setTimeout(() => window.clearInterval(appendHandler), 60000)

    val dropdown = UdashDropdown(items)(
      UdashDropdown.defaultItemFactory,
      _ => Seq[Modifier]("Dropdown ", BootstrapStyles.Button.color(Color.Primary))
    )
    val dropup = UdashDropdown(items, UdashDropdown.Direction.Up.toProperty)(UdashDropdown.defaultItemFactory, _ => "Dropup ")
    val listener: PartialFunction[UdashDropdown.DropdownEvent[DefaultDropdownItem, CastableProperty[DefaultDropdownItem]], Unit] = {
      case UdashDropdown.DropdownEvent.SelectionEvent(_, item) => clicks.append(item.toString)
      case ev: DropdownEvent[_, _] => logger.info(ev.toString)
    }

    dropdown.listen(listener)
    dropup.listen(listener)

    div(GuideStyles.frame)(
      div(BootstrapStyles.Grid.row)(
        div(BootstrapStyles.Grid.col(6))(dropdown.render),
        div(BootstrapStyles.Grid.col(6))(dropup.render)
      ),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(BootstrapStyles.Border.border())(seq.map(click =>
          li(click)
        ): _*).render
      )
    ).render
  }

  def buttonsDemo(): dom.Element = {
    val smallBtn = Some(Size.Small).toProperty[Option[Size]]
    val disabledButtons = Property(Set.empty[Int])
    def disabled(idx: Int): ReadableProperty[Boolean] = disabledButtons.transform(_.contains(idx))
    val buttons = Seq(
      UdashButton(Color.Primary.toProperty, smallBtn, disabled = disabled(0))(_ => Seq[Modifier]("Primary", GlobalStyles.smallMargin)),
      UdashButton(Color.Secondary.toProperty, smallBtn, disabled = disabled(1))(_ => Seq[Modifier]("Secondary", GlobalStyles.smallMargin)),
      UdashButton(Color.Success.toProperty, smallBtn, disabled = disabled(2))(_ => Seq[Modifier]("Success", GlobalStyles.smallMargin)),
      UdashButton(Color.Info.toProperty, smallBtn, disabled = disabled(3))(_ => Seq[Modifier]("Info", GlobalStyles.smallMargin)),
      UdashButton(Color.Warning.toProperty, smallBtn, disabled = disabled(4))(_ => Seq[Modifier]("Warning", GlobalStyles.smallMargin)),
      UdashButton(Color.Danger.toProperty, smallBtn, disabled = disabled(5))(_ => Seq[Modifier]("Danger", GlobalStyles.smallMargin)),
      UdashButton(Color.Link.toProperty, smallBtn, disabled = disabled(6))(_ => Seq[Modifier]("Link", GlobalStyles.smallMargin)),
      UdashButton(Color.Light.toProperty, smallBtn, disabled = disabled(7))(_ => Seq[Modifier]("Light", GlobalStyles.smallMargin)),
      UdashButton(Color.Dark.toProperty, smallBtn, disabled = disabled(8))(_ => Seq[Modifier]("Dark", GlobalStyles.smallMargin)),
      UdashButton(Color.White.toProperty, smallBtn, disabled = disabled(9))(_ => Seq[Modifier]("White", GlobalStyles.smallMargin)),
    )

    val clicks = SeqProperty[String](Seq.empty)
    buttons.foreach(_.listen {
      case UdashButton.ButtonClickEvent(source, _) => clicks.append(source.render.textContent)
    })

    val push = UdashButton(size = Some(Size.Large).toProperty[Option[Size]], block = true.toProperty)("Disable random buttons!")
    push.listen { case UdashButton.ButtonClickEvent(_, _) =>
      clicks.set(Seq.empty)

      val disabledCount = Random.nextInt(buttons.size + 1)
      disabledButtons.set(Seq.fill(disabledCount)(Random.nextInt(buttons.size)).toSet)
    }

    div(GuideStyles.frame)(
      push.render,
      div(GlobalStyles.centerBlock)(
        buttons.map(_.render)
      ),
      h4("Clicks: "),
      produce(clicks)(seq =>
        ul(BootstrapStyles.Border.border())(seq.map(li(_))).render
      )
    ).render
  }

  def toggleButton(): dom.Element = {
    val buttons = Color.values.map { color =>
      color.name -> {
        val active = Property(false)
        val btn = UdashButton.toggle(active, color.toProperty[Color])(_ => Seq[Modifier](color.name, GlobalStyles.smallMargin))
        (active, btn)
      }
    }

    div(GuideStyles.frame)(
      div(GlobalStyles.centerBlock)(
        buttons.map { case (_, (_, btn)) => btn.render }
      ),
      h4("Is active: "),
      div(BootstrapStyles.Border.border())(
        buttons.map({ case (name, (active, _)) =>
          span(s"$name: ", bind(active), br)
        }).toSeq
      )
    ).render
  }

  def staticButtonsGroup(): dom.Element = {
    div(GuideStyles.frame)(
      UdashButtonGroup(vertical = true.toProperty)(
        UdashButton(buttonStyle = Color.Primary.toProperty)("Button 1").render,
        UdashButton()("Button 2").render,
        UdashButton()("Button 3").render
      ).render
    ).render
  }

  def buttonToolbar(): dom.Element = {
    val groups = SeqProperty[Seq[Int]](Seq[Seq[Int]](1 to 4, 5 to 7, 8 to 8))
    div(GuideStyles.frame)(
      UdashButtonToolbar.reactive(groups)((p: CastableProperty[Seq[Int]], nested) => {
        val range = p.asSeq[Int]
        val group = UdashButtonGroup.reactive(range, size = Some(Size.Large).toProperty[Option[Size]]) {
          case (element, nested) =>
            val btn = UdashButton()(_ => nested(bind(element)))
            nested(btn)
            btn.render
        }
        nested(group)
        group.render
      }).render
    ).render
  }

  def checkboxButtons(): dom.Element = {
    val selected = SeqProperty[String]("Checkbox 1")
    val options = SeqProperty[String]("Checkbox 1", "Checkbox 2", "Checkbox 3")
    div(GuideStyles.frame)(
      UdashButtonGroup.checkboxes(selected, options)().render,
      h4("Is active: "),
      div(BootstrapStyles.Border.border())(
        repeatWithNested(options) { (option, nested) =>
          val checked = selected.transform((_: Seq[String]).contains(option.get))
          div(bind(option), ": ", bind(checked)).render
        }
      )
    ).render
  }

  def radioButtons(): dom.Element = {
    val selected = Property[String]("Radio 1")
    val options = SeqProperty[String]("Radio 1", "Radio 2", "Radio 3")
    div(GuideStyles.frame)(
      UdashButtonGroup.radio(selected, options)().render,
      h4("Is active: "),
      div(BootstrapStyles.Border.border())(
        repeatWithNested(options) { (option, nested) =>
          val checked = selected.transform(_ == option.get)
          div(bind(option), ": ", bind(checked)).render
        }
      )
    ).render
  }

  def buttonDropdown(): dom.Element = {
    val items = SeqProperty[DefaultDropdownItem](
      UdashDropdown.DefaultDropdownItem.Header("Start"),
      UdashDropdown.DefaultDropdownItem.Link("Intro", Url("#")),
      UdashDropdown.DefaultDropdownItem.Disabled(UdashDropdown.DefaultDropdownItem.Link("Test Disabled", Url("#"))),
      UdashDropdown.DefaultDropdownItem.Divider,
      UdashDropdown.DefaultDropdownItem.Header("End"),
      UdashDropdown.DefaultDropdownItem.Link("Intro", Url("#"))
    )
    div(GuideStyles.frame)(
      UdashButtonToolbar()(
        UdashButtonGroup()(
          UdashButton()("Button").render,
          UdashDropdown(items)(UdashDropdown.defaultItemFactory, _ => "").render,
          UdashDropdown(items, dropDirection = UdashDropdown.Direction.Up.toProperty)(UdashDropdown.defaultItemFactory, _ => "").render
        ).render,
        UdashDropdown(items)(UdashDropdown.defaultItemFactory, _ => "Dropdown ").render
      ).render
    ).render
  }

  def inputGroups(): dom.Element = {
    val vanityUrl = Property.blank[String]
    val buttonDisabled = Property(true)
    vanityUrl.listen(v => buttonDisabled.set(v.isEmpty))
    val button = UdashButton()("Clear")
    button.listen { case _ => vanityUrl.set("") }
    div(GuideStyles.frame)(
      label("Your URL"),
      UdashInputGroup(groupSize = Some(BootstrapStyles.Size.Large).toProperty)(
        UdashInputGroup.prependText("https://example.com/users/", bind(vanityUrl)),
        UdashInputGroup.input(TextInput(vanityUrl)().render),
        UdashInputGroup.append(
          UdashButton(disabled = buttonDisabled)("Go!").render,
          button.render
        )
      ).render
    ).render
  }

  def simpleForm(): dom.Element = {
    sealed trait ShirtSize
    case object Small extends ShirtSize
    case object Medium extends ShirtSize
    case object Large extends ShirtSize

    def shirtSizeToLabel(size: ShirtSize): String = size match {
      case Small => "S"
      case Medium => "M"
      case Large => "L"
    }

    trait UserModel {
      def name: String
      def age: Int
      def shirtSize: ShirtSize
    }
    object UserModel extends HasModelPropertyCreator[UserModel] {
      implicit val blank: Blank[UserModel] = Blank.Simple(new UserModel {
        override def name: String = ""
        override def age: Int = 25
        override def shirtSize: ShirtSize = Medium
      })
    }

    val user = ModelProperty.blank[UserModel]
    user.subProp(_.age).addValidator(new Validator[Int] {
      override def apply(element: Int): Future[ValidationResult] =
        Future {
          if (element < 0) Invalid("Age should be a non-negative integer!")
          else Valid
        }
    })

    div(GuideStyles.frame)(
      UdashForm()(factory => Seq(
        factory.input.formGroup()(
          input = _ => factory.input.textInput(user.subProp(_.name))().render,
          labelContent = Some(_ => "User name": Modifier)
        ),
        factory.input.formGroup()(
          input = _ => factory.input.numberInput(
            user.subProp(_.age).transform(_.toString, _.toInt),
          )().render,
          labelContent = Some(_ => "Age": Modifier),
          invalidFeedback = Some(_ => "Age should be a non-negative integer!")
        ),
        factory.input.radioButtons(
          user.subProp(_.shirtSize),
          Seq[ShirtSize](Small, Medium, Large).toSeqProperty,
          inline = true.toProperty,
          validationTrigger = UdashForm.ValidationTrigger.None
        )(labelContent = (item, _, _) => Some(label(shirtSizeToLabel(item)))),
        factory.disabled()(_ => UdashButton()("Send").render)
      )).render
    ).render
  }

  def inlineForm(): dom.Element = {
    val search = Property.blank[String]
    val something = Property.blank[String]
    div(GuideStyles.frame)(
      UdashForm(inline = true)(factory => Seq(
        UdashInputGroup()(
          UdashInputGroup.prependText("Search: "),
          UdashInputGroup.input(factory.input.textInput(search)().render)
        ).render,
        UdashInputGroup()(
          UdashInputGroup.prependText("Something: "),
          UdashInputGroup.input(factory.input.textInput(something)().render)
        ).render,
      )).render
    ).render
  }

  def navs(): dom.Element = {
    trait Panel {
      def title: String
      def content: String
    }
    object Panel extends HasModelPropertyCreator[Panel]
    case class DefaultPanel(override val title: String, override val content: String) extends Panel

    val panels = SeqProperty[Panel](
      DefaultPanel("Title 1", "Content of panel 1..."),
      DefaultPanel("Title 2", "Content of panel 2..."),
      DefaultPanel("Title 3", "Content of panel 3..."),
      DefaultPanel("Title 4", "Content of panel 4...")
    )
    val selected = Property[Panel](panels.elemProperties.head.get)
    panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
    div(GuideStyles.frame)(
      UdashNav(panels, justified = true.toProperty, tabs = true.toProperty)(
        elemFactory = (panel, nested) => a(
          href := "",
          onclick :+= ((ev: Event) => selected.set(panel.get), true)
        )(nested(bind(panel.asModel.subProp(_.title)))).render,
        isActive = panel => panel.combine(selected)((panel, selected) => panel.title == selected.title)
      ).render,
      div(
        BootstrapStyles.Border.border(BootstrapStyles.Side.Left),
        BootstrapStyles.Border.border(BootstrapStyles.Side.Right),
        BootstrapStyles.Border.border(BootstrapStyles.Side.Bottom),
        BootstrapStyles.Spacing.padding()
      )(
        bind(selected.asModel.subProp(_.content))
      )
    ).render
  }

  def navbars(): dom.Element = {
    trait Panel {
      def title: String
      def content: String
    }
    object Panel extends HasModelPropertyCreator[Panel]

    case class DefaultPanel(override val title: String, override val content: String) extends Panel

    val panels = SeqProperty[Panel](
      DefaultPanel("Title 1", "Content of panel 1..."),
      DefaultPanel("Title 2", "Content of panel 2..."),
      DefaultPanel("Title 3", "Content of panel 3..."),
      DefaultPanel("Title 4", "Content of panel 4...")
    )
    panels.append(DefaultPanel("Title 5", "Content of panel 5..."))
    div(GuideStyles.frame)(
      UdashNavbar()(
        _ => UdashNav(panels)(
          elemFactory = (panel, nested) => a(href := "", onclick :+= ((ev: Event) => true))(
            nested(bind(panel.asModel.subProp(_.title)))
          ).render,
          isActive = el => el.transform(_.title.endsWith("1")),
          isDisabled = el => el.transform(_.title.endsWith("5"))
        ),
        span("Udash"),
      ).render
    ).render
  }

  def udashNavigation(): dom.Element = {
    def linkFactory(l: MenuLink) =
      a(href := l.state.url, BootstrapStyles.Dropdown.item)(span(l.name)).render

    val panels = SeqProperty[MenuEntry](mainMenuEntries.slice(0, 4))
    div(GuideStyles.frame)(
      UdashNavbar(darkStyle = true.toProperty, backgroundStyle = BootstrapStyles.Color.Dark.toProperty)(
        _ => UdashNav(panels)(
          elemFactory = (panel, _) => panel.get match {
            case MenuContainer(name, children) =>
              val childrenProperty = SeqProperty(children)
              UdashDropdown(childrenProperty, buttonToggle = false.toProperty)(
                (item: Property[MenuLink], _) => linkFactory(item.get),
                _ => span(name, " ")
              ).render
            case link: MenuLink => linkFactory(link)
          },
          isDropdown = _.transform {
            case MenuContainer(_, _) => true
            case MenuLink(_, _, _) => false
          }
        ),
        span("Udash"),
      ).render
    ).render
  }

  def breadcrumbs(): dom.Element = {
    import UdashBreadcrumbs._

    val pages = SeqProperty[Breadcrumb](
      new Breadcrumb("Udash", Url("http://udash.io/")),
      new Breadcrumb("Dev's Guide", Url("http://guide.udash.io/")),
      new Breadcrumb("Extensions", Url("http://guide.udash.io/")),
      new Breadcrumb("Bootstrap wrapper", Url("http://guide.udash.io/ext/bootstrap"))
    )

    val breadcrumbs = UdashBreadcrumbs(pages)(
      defaultPageFactory,
      pages.get.last == _
    )
    div(GuideStyles.frame)(
      breadcrumbs.render
    ).render
  }

  def pagination(): dom.Element = {
    import UdashPagination._

    val showArrows = Property(true)
    val highlightActive = Property(true)
    val toggleArrows = UdashButton.toggle(active = showArrows)("Toggle arrows")
    val toggleHighlight = UdashButton.toggle(active = highlightActive)("Toggle highlight")

    val pages = SeqProperty(0 to 7)
    val selected = Property(0)
    val pagination = UdashPagination(
      pages, selected,
      showArrows = showArrows, highlightActive = highlightActive
    )(defaultPageFactory)
    div(GuideStyles.frame)(
      div(
        UdashButtonGroup()(
          toggleArrows.render,
          toggleHighlight.render
        ).render
      ),
      div("Selected page index: ", bind(selected)),
      div(GlobalStyles.centerBlock)(pagination.render)
    ).render
  }

  def labels(): dom.Element = {
    div(GuideStyles.frame)(
      div(
        UdashBadge(badgeStyle = BootstrapStyles.Color.Primary.toProperty)(_ => "Primary").render,
        UdashBadge(badgeStyle = BootstrapStyles.Color.Secondary.toProperty, pillStyle = true.toProperty)(_ => "Secondary Pill").render,
        UdashBadge.link(Property("https://udash.io/"), badgeStyle = BootstrapStyles.Color.Success.toProperty)(_ => "Success Link").render,
        UdashBadge(badgeStyle = BootstrapStyles.Color.Danger.toProperty)(_ => "Danger").render,
        UdashBadge(badgeStyle = BootstrapStyles.Color.Warning.toProperty)(_ => "Warning").render,
        UdashBadge(badgeStyle = BootstrapStyles.Color.Info.toProperty)(_ => "Info").render,
        UdashBadge(badgeStyle = BootstrapStyles.Color.Light.toProperty)(_ => "Light").render,
        UdashBadge(badgeStyle = BootstrapStyles.Color.Dark.toProperty)(_ => "Dark").render,
      )
    ).render
  }

  def badges(): dom.Element = {
    val counter = Property(0)
    window.setInterval(() => counter.set(counter.get + 1), 3000)
    div(GuideStyles.frame)(
      div(
        UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty, size = Some(BootstrapStyles.Size.Large).toProperty)(
          _ => Seq[Modifier]("Button ", UdashBadge()(nested => nested(bind(counter))).render)
        ).render
      )
    ).render
  }

  def alerts(): dom.Element = {
    val dismissed = SeqProperty[String](Seq.empty)
    def randomDismissible(): dom.Element = {
      val title = randomString()
      val alert = DismissibleUdashAlert(
        alertStyle = BootstrapStyles.Color.values(Random.nextInt(BootstrapStyles.Color.values.size)).toProperty
      )(title)
      alert.dismissed.listen(_ => dismissed.append(title))
      alert.render
    }
    val alerts = div(GlobalStyles.centerBlock)(
      UdashAlert(alertStyle = BootstrapStyles.Color.Info.toProperty)("info").render,
      UdashAlert(alertStyle = BootstrapStyles.Color.Success.toProperty)("success").render,
      UdashAlert(alertStyle = BootstrapStyles.Color.Warning.toProperty)("warning").render,
      UdashAlert(alertStyle = BootstrapStyles.Color.Danger.toProperty)("danger").render
    ).render
    val create = UdashButton()("Create dismissible alert")
    create.listen { case _ => alerts.appendChild(randomDismissible()) }
    div(GuideStyles.frame)(
      alerts,
      create.render,
      h4("Dismissed: "),
      produce(dismissed)(seq =>
        ul(seq.map(click => li(click))).render
      )
    ).render
  }

  def progressBar(): dom.Element = {
    val showPercentage = Property(true)
    val animate = Property(true)
    val value = Property(50)
    div(GuideStyles.frame)(
      div(
        UdashButtonGroup()(
          UdashButton.toggle(active = showPercentage)("Show percentage").render,
          UdashButton.toggle(active = animate)("Animate").render
        ).render
      ), br,
      UdashProgressBar(value, showPercentage, barStyle = Some(BootstrapStyles.Color.Success).toProperty)().render,
      UdashProgressBar(value, showPercentage, stripped = true.toProperty)(
        (value, min, max, nested) => Seq[Modifier](
          nested(bind(value.combine(min)(_ - _).combine(max.combine(min)(_ - _))(_ * 100 / _))),
          " percent"
        )
      ).render,
      UdashProgressBar(value, showPercentage, stripped = true.toProperty, animated = animate, barStyle = Some(BootstrapStyles.Color.Danger).toProperty)().render,
      NumberInput(value.transform(_.toString, Integer.parseInt))(
        BootstrapStyles.Form.control, placeholder := "Percentage"
      )
    ).render
  }

  def listGroup(): dom.Element = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
    val listGroup = UdashListGroup(news)((news, nested) =>
      li(
        nested(BootstrapStyles.active.styleIf(news.transform(_.endsWith("1")))),
        nested(BootstrapStyles.disabled.styleIf(news.transform(_.endsWith("2")))),
        nested(BootstrapStyles.List.color(BootstrapStyles.Color.Success).styleIf(news.transform(_.endsWith("3")))),
        nested(BootstrapStyles.List.color(BootstrapStyles.Color.Danger).styleIf(news.transform(_.endsWith("4")))),
        nested(BootstrapStyles.List.color(BootstrapStyles.Color.Info).styleIf(news.transform(_.endsWith("5")))),
        nested(BootstrapStyles.List.color(BootstrapStyles.Color.Warning).styleIf(news.transform(_.endsWith("6"))))
      )(nested(bind(news))).render
    )

    var i = 1
    val appendHandler = window.setInterval(() => {
      news.append(s"Dynamic $i")
      i += 1
    }, 2000)
    window.setTimeout(() => window.clearInterval(appendHandler), 20000)

    div(GuideStyles.frame)(
      listGroup.render
    ).render
  }

  def cards(): dom.Element = {
    val news = SeqProperty[String]("Title 1", "Title 2", "Title 3")
    div(GuideStyles.frame)(
      UdashCard(
        borderColor = Some(BootstrapStyles.Color.Success).toProperty,
        textColor = Some(BootstrapStyles.Color.Primary).toProperty,
      )(factory => Seq(
        factory.header("Card heading"),
        factory.body("Some default panel content here. Nulla vitae elit libero, a pharetra augue. Aenean lacinia bibendum nulla sed consectetur. Aenean eu leo quam. Pellentesque ornare sem lacinia quam venenatis vestibulum. Nullam id dolor id nibh ultricies vehicula ut id elit."),
        factory.listGroup(nested => {
          val group = UdashListGroup(news)((news, nested) => li(nested(bind(news))).render)
          nested(group)
          group
        }),
        factory.footer("Card footer")
      )).render
    ).render
  }

  def responsiveEmbed(): dom.Element = {
    div(GuideStyles.frame)(
      div(BootstrapStyles.EmbedResponsive.responsive, BootstrapStyles.EmbedResponsive.embed16by9, GlobalStyles.smallMargin)(
        iframe(BootstrapStyles.EmbedResponsive.item, src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0")
      ),
      div(BootstrapStyles.EmbedResponsive.responsive, BootstrapStyles.EmbedResponsive.embed4by3, GlobalStyles.smallMargin)(
        iframe(BootstrapStyles.EmbedResponsive.item, src := "http://www.youtube.com/embed/zpOULjyy-n8?rel=0")
      )
    ).render
  }

  def simpleModal(): dom.Element = {
    val events = SeqProperty.blank[UdashModal.ModalEvent]
    val header = (_: Binding.NestedInterceptor) => div("Modal events").render
    val body = (nested: Binding.NestedInterceptor) => div(ul(nested(repeat(events)(event => li(event.get.toString).render)))).render
    val footer = (_: Binding.NestedInterceptor) => div(
      UdashButton()(_ => Seq[Modifier](UdashModal.CloseButtonAttr, "Close")).render,
      UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)("Something...").render
    ).render

    val modal = UdashModal(modalSize = Some(BootstrapStyles.Size.Large).toProperty)(
      headerFactory = Some(header),
      bodyFactory = Some(body),
      footerFactory = Some(footer)
    )
    modal.listen { case ev => events.append(ev) }

    val openModalButton = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)("Show modal...")
    openModalButton.listen { case _ =>
      modal.show()
    }
    val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
    openAndCloseButton.listen { case _ =>
      modal.show()
      window.setTimeout(() => modal.hide(), 2000)
    }
    div(GuideStyles.frame)(
      modal.render,
      UdashButtonGroup()(
        openModalButton.render,
        openAndCloseButton.render
      ).render
    ).render
  }

  def tooltips(): dom.Element = {
    import scala.concurrent.duration.DurationInt
    val label1 = UdashBadge()(_ => Seq[Modifier]("Tooltip on hover with delay", GlobalStyles.smallMargin)).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Hover),
      delay = UdashTooltip.Delay(500 millis, 250 millis),
      title = (_) => "Tooltip..."
    )(label1)

    val label2 = UdashBadge()(_ => Seq[Modifier]("Tooltip on click", GlobalStyles.smallMargin)).render
    UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Click),
      delay = UdashTooltip.Delay(0 millis, 250 millis),
      placement = (_, _) => Seq(UdashTooltip.Placement.Bottom),
      title = (_) => "Tooltip 2..."
    )(label2)

    val label3 = UdashBadge()(_ => Seq[Modifier]("Tooltip with JS toggler", GlobalStyles.smallMargin)).render
    val label3Tooltip = UdashTooltip(
      trigger = Seq(UdashTooltip.Trigger.Manual),
      placement = (_, _) => Seq(UdashTooltip.Placement.Right),
      title = (_) => "Tooltip 3..."
    )(label3)

    val button = UdashButton()("Toggle tooltip")
    button.listen { case _ => label3Tooltip.toggle() }

    div(GuideStyles.frame)(
      label1, label2, label3, button.render
    ).render
  }

  def popovers(): dom.Element = {
    import scala.concurrent.duration.DurationInt
    val label1 = UdashBadge()(_ => Seq[Modifier]("Popover on hover with delay", GlobalStyles.smallMargin)).render
    UdashPopover(
      trigger = Seq(UdashPopover.Trigger.Hover),
      delay = UdashPopover.Delay(500 millis, 250 millis),
      title = (_) => "Popover...",
      content = (_) => "Content..."
    )(label1)

    val label2 = UdashBadge()(_ => Seq[Modifier]("Popover on click", GlobalStyles.smallMargin)).render
    UdashPopover(
      trigger = Seq(UdashPopover.Trigger.Click),
      delay = UdashPopover.Delay(0 millis, 250 millis),
      placement = (_, _) => Seq(UdashPopover.Placement.Bottom),
      title = (_) => "Popover 2...",
      content = (_) => "Content..."
    )(label2)

    val label3 = UdashBadge()(_ => Seq[Modifier]("Popover with JS toggler", GlobalStyles.smallMargin)).render
    val label3Tooltip = UdashPopover(
      trigger = Seq(UdashPopover.Trigger.Manual),
      placement = (_, _) => Seq(UdashPopover.Placement.Left),
      html = true,
      title = (_) => "Popover 3...",
      content = (_) => {
        import scalatags.Text.all._
        Seq(
          p("HTML content..."),
          ul(li("Item 1"), li("Item 2"), li("Item 3"))
        ).render
      }
    )(label3)

    val button = UdashButton()("Toggle popover")
    button.listen { case _ => label3Tooltip.toggle() }

    div(GuideStyles.frame)(
      label1, label2, label3, button.render
    ).render
  }

  def simpleCollapse(): dom.Element = {
    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val collapse = UdashCollapse()(
      div(BootstrapStyles.Border.border())(
        ul(repeat(events)(event => li(event.get.toString).render))
      )
    )
    collapse.listen { case ev => events.append(ev) }

    val toggleButton = UdashButton(buttonStyle = BootstrapStyles.Color.Primary.toProperty)(
      _ => Seq[Modifier](collapse.toggleButtonAttrs(), "Toggle...")
    )
    val openAndCloseButton = UdashButton()("Open and close after 2 seconds...")
    openAndCloseButton.listen { case _ =>
      collapse.show()
      window.setTimeout(() => collapse.hide(), 2000)
    }

    div(GuideStyles.frame)(
      UdashButtonGroup(justified = true.toProperty)(
        toggleButton.render,
        openAndCloseButton.render
      ).render,
      collapse.render
    ).render
  }

  def accordionCollapse(): dom.Element = {
    val events = SeqProperty.blank[UdashCollapse.CollapseEvent]
    val news = SeqProperty[String](
      "Title 1", "Title 2", "Title 3"
    )

    val accordion = UdashAccordion(news)(
      (news, _) => span(news.get).render,
      (_, _) => ul(repeat(events)(event => li(event.get.toString).render)).render
    )

    val accordionElement = accordion.render
    news.elemProperties.map(accordion.collapseOf)
      .filter(_.isDefined)
      .foreach(_.get.listen { case ev => events.append(ev) })

    div(GuideStyles.frame)(accordionElement).render
  }

  def carousel(): dom.Element = {
//    def newSlide(): UdashCarouselSlide = UdashCarouselSlide(
//      Url("/assets/images/ext/bootstrap/carousel.jpg")
//    )(
//      h3(randomString()),
//      p(randomString())
//    )
//    val slides = SeqProperty[UdashCarouselSlide]((1 to 5).map(_ => newSlide()))
//    val active = Property(true)
//    import scala.concurrent.duration._
//    val carousel = UdashCarousel(slides, activeSlide = 1,
//      animationOptions = AnimationOptions(interval = 2 seconds, keyboard = false, active = active.get)
//    )
//    val prevButton = UdashButton()("Prev")
//    val nextButton = UdashButton()("Next")
//    val prependButton = UdashButton()("Prepend")
//    val appendButton = UdashButton()("Append")
//    prevButton.listen { case _ => carousel.previousSlide() }
//    nextButton.listen { case _ => carousel.nextSlide() }
//    prependButton.listen { case _ => slides.prepend(newSlide()) }
//    appendButton.listen { case _ => slides.append(newSlide()) }
//    active.listen(b => if (b) carousel.cycle() else carousel.pause())
    div(
//      div(GuideStyles.frame)(
//        UdashButtonToolbar(
//          UdashButton.toggle(active = active)("Run animation").render,
//          UdashButtonGroup()(
//            prevButton.render,
//            nextButton.render
//          ).render,
//          UdashButtonGroup()(
//            prependButton.render,
//            appendButton.render
//          ).render
//        ).render
//      ),
//      div(
//        carousel.render
//      )
    ).render
  }

  def jumbotron(): dom.Element =
    UdashJumbotron()( _ => Seq[Modifier](
      h1("Jumbo poem!"),
      p("One component to rule them all, one component to find them, one component to bring them all and in the darkness bind them."),
      UdashButton(buttonStyle = Color.Info.toProperty, size = Some(Size.Large).toProperty[Option[Size]])(_ => "Click").render
    )).render

  private def randomString(): String = Random.nextLong().toString
}
