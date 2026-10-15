package com.quillfold.reader

import android.os.Bundle
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

private val Ink = Color(0xFF30251D)
private val Paper = Color(0xFFF1E6CC)
private val PaperLight = Color(0xFFFAF3E3)
private val Leather = Color(0xFF583A28)
private val Brass = Color(0xFFAE7B36)
private val Moss = Color(0xFF66704A)
private val Muted = Color(0xFF786A56)

data class Story(val title: String, val source: String, val time: String, val tag: String, val summary: String, val url: String)
private val stories = listOf(
    Story("The next wave of AI is being built in open source", "MIT Technology Review", "12 min ago", "OPEN MODELS", "Researchers and startups are reshaping the frontier with models anyone can inspect, adapt, and run.", "https://www.technologyreview.com/topic/artificial-intelligence/"),
    Story("A new map of how language models reason", "Google DeepMind", "38 min ago", "RESEARCH", "A closer look at the internal steps that help models solve multi-stage problems.", "https://deepmind.google/discover/blog/"),
    Story("Small models, surprisingly big ideas", "The Batch · deeplearning.ai", "1 hr ago", "INDUSTRY", "Efficient systems are bringing useful AI to devices that used to depend on the cloud.", "https://www.deeplearning.ai/the-batch/"),
    Story("What AI agents can actually do today", "Hugging Face Blog", "2 hrs ago", "AGENTS", "A practical tour through the tools, limits, and design choices behind autonomous workflows.", "https://huggingface.co/blog"),
    Story("The case for slower, more careful AI", "arXiv · cs.AI", "3 hrs ago", "PAPERS", "New work explores evaluation methods that reward reliability over quick answers.", "https://arxiv.org/list/cs.AI/recent"),
    Story("When the machine learns to see", "Google Research", "4 hrs ago", "VISION", "New multimodal systems find useful patterns across images, text, and sound.", "https://research.google/blog/"),
    Story("The quiet craft of a better dataset", "The Gradient", "5 hrs ago", "DATA", "Careful curation continues to shape how capable and reliable models become.", "https://thegradient.pub/"),
    Story("AI and the future of work", "Stanford HAI", "6 hrs ago", "SOCIETY", "Research on how emerging systems are changing people, workplaces, and institutions.", "https://hai.stanford.edu/news"),
    Story("Fresh papers in machine learning", "arXiv · cs.LG", "7 hrs ago", "PAPERS", "New preprints spanning machine learning methods, theory, and applications.", "https://arxiv.org/list/cs.LG/recent"),
    Story("Building useful language tools", "Anthropic News", "8 hrs ago", "LAB NOTES", "Updates and ideas from a research lab working on reliable AI systems.", "https://www.anthropic.com/news"),
    Story("AI research and engineering", "Meta AI", "9 hrs ago", "RESEARCH", "Research notes on open models, computer vision, language, and more.", "https://ai.meta.com/blog/"),
    Story("The latest from Microsoft Research AI", "Microsoft Research", "10 hrs ago", "RESEARCH", "Stories from teams exploring the foundations and applications of artificial intelligence.", "https://www.microsoft.com/en-us/research/research-area/artificial-intelligence/"),
    Story("Machine learning, explained", "Distill Archive", "Yesterday", "EXPLAINERS", "Visual essays that make complex machine learning concepts easier to explore.", "https://distill.pub/"),
    Story("AI policy and public interest", "AI Now Institute", "Yesterday", "POLICY", "Reports and analysis on the social implications of artificial intelligence.", "https://ainowinstitute.org/"),
    Story("The latest in robotics and AI", "IEEE Spectrum", "Yesterday", "ROBOTICS", "Engineering coverage of intelligent machines, automation, and robotics.", "https://spectrum.ieee.org/artificial-intelligence")
)
private val feedSources = listOf("MIT Technology Review", "Google DeepMind", "The Batch · deeplearning.ai", "Hugging Face Blog", "arXiv · cs.AI", "arXiv · cs.LG", "Google Research", "Stanford HAI", "Anthropic News", "Meta AI", "Microsoft Research AI", "The Gradient", "Distill Archive", "AI Now Institute", "IEEE Spectrum")
private val sections = listOf("The daily edition", "Research", "Industry", "Open source", "Saved")

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = android.graphics.Color.rgb(241, 230, 204)
        window.navigationBarColor = android.graphics.Color.rgb(241, 230, 204)
        setContent { QuillfoldApp() }
    }
}

@Composable
fun QuillfoldApp() {
    var selected by remember { mutableIntStateOf(0) }
    var page by remember { mutableIntStateOf(0) }
    var settings by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current
    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    fun turnPage(forward: Boolean) {
        page = (page + if (forward) 1 else -1).coerceAtLeast(0)
        scope.launch {
            rotation.snapTo(if (forward) 0f else -180f)
            rotation.animateTo(if (forward) -180f else 0f, tween(340))
            rotation.snapTo(0f)
        }
    }
    MaterialTheme(colorScheme = lightColorScheme(primary = Leather, secondary = Brass, background = Paper, surface = PaperLight)) {
        BoxWithConstraints(Modifier.fillMaxSize().background(Paper)) {
            val wide = maxWidth > 650.dp
            Column(Modifier.fillMaxSize().pointerInput(page, wide, settings) {
                var horizontalDrag = 0f
                detectHorizontalDragGestures(onDragEnd = {
                    if (!settings && horizontalDrag < -60f) turnPage(true) else if (!settings && horizontalDrag > 60f) turnPage(false)
                    horizontalDrag = 0f
                }, onHorizontalDrag = { change, amount -> change.consume(); horizontalDrag += amount })
            }) {
                Header(onSettings = { settings = true })
                Row(Modifier.fillMaxWidth().weight(1f).padding(horizontal = if (wide) 20.dp else 8.dp, vertical = 4.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (wide) LibraryRail(selected, onSelect = { selected = it; page = 0 })
                    Column(Modifier.weight(1f).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
                        EditionBar(selected, page, onPrevious = { turnPage(false) }, onNext = { turnPage(true) })
                        if (wide) {
                            Row(Modifier.fillMaxSize().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(0.dp)) {
                                PageSheet(stories.drop((page * 2) % stories.size).take(3), Modifier.weight(1f).fillMaxHeight(), page * 2 + 1, onOpen = { openArticle(context, it) })
                                Spine(Modifier.width(22.dp).fillMaxHeight())
                                PageSheet(stories.drop((page * 2 + 3) % stories.size).take(3), Modifier.weight(1f).fillMaxHeight(), page * 2 + 2, onOpen = { openArticle(context, it) })
                            }
                        } else {
                            PageSheet(stories.drop((page * 2) % stories.size).take(2), Modifier.fillMaxSize().padding(bottom = 8.dp), page + 1, onOpen = { openArticle(context, it) })
                        }
                    }
                }
            }
            if (settings) FeedSettings(onClose = { settings = false })
        }
    }
}

@Composable
private fun Header(onSettings: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Leather).padding(horizontal = 20.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(35.dp).clip(RoundedCornerShape(7.dp)).background(Brass), contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoStories, null, tint = PaperLight, modifier = Modifier.size(23.dp)) }
        Spacer(Modifier.width(11.dp)); Text("quillfold", fontFamily = FontFamily.Serif, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = PaperLight)
        Spacer(Modifier.weight(1f)); Text("A FIELD GUIDE TO AI", color = Color(0xFFE1CDA8), fontSize = 9.sp, letterSpacing = 1.6.sp)
        Spacer(Modifier.width(9.dp)); IconButton(onClick = onSettings) { Icon(Icons.Default.Tune, "Feed settings", tint = PaperLight) }
    }
}

private fun openArticle(context: android.content.Context, url: String) {
    runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
}

@Composable
private fun LibraryRail(selected: Int, onSelect: (Int) -> Unit) {
    Column(Modifier.width(174.dp).fillMaxHeight().padding(top = 20.dp)) {
        Text("THE SHELF", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, modifier = Modifier.padding(start = 12.dp, bottom = 12.dp))
        sections.forEachIndexed { i, label ->
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp).clip(RoundedCornerShape(8.dp)).background(if (selected == i) Color(0xFFE2D2B3) else Color.Transparent).clickable { onSelect(i) }.padding(horizontal = 11.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(if (i == 0) Icons.Default.AutoAwesome else if (i == 4) Icons.Default.Bookmark else Icons.Default.MenuBook, null, tint = if (selected == i) Brass else Muted, modifier = Modifier.size(17.dp))
                Spacer(Modifier.width(10.dp)); Text(label, color = Ink, fontSize = 13.sp, fontWeight = if (selected == i) FontWeight.Bold else FontWeight.Normal)
            }
        }
        Spacer(Modifier.weight(1f)); Text("EST. FOR THE CURIOUS", color = Muted, fontSize = 8.sp, letterSpacing = 1.1.sp, modifier = Modifier.padding(12.dp))
    }
}

@Composable
private fun EditionBar(selected: Int, page: Int, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(sections.getOrElse(selected) { sections.first() }, fontFamily = FontFamily.Serif, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Ink)
            Text("TUESDAY, OCTOBER 15   •   THE AI JOURNAL", fontSize = 9.sp, color = Muted, letterSpacing = 1.1.sp)
        }
        IconButton(onClick = onPrevious) { Icon(Icons.Default.ChevronLeft, "Previous page", tint = Leather) }
        Text("${page + 1}", color = Leather, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Text(" / 12", color = Muted, fontSize = 12.sp)
        IconButton(onClick = onNext) { Icon(Icons.Default.ChevronRight, "Next page", tint = Leather) }
    }
}

@Composable
private fun Spine(modifier: Modifier = Modifier) {
    Box(modifier.background(Color(0xFFB9A17B)), contentAlignment = Alignment.Center) {
        Box(Modifier.width(2.dp).fillMaxHeight().background(Color(0xFF806443)))
        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.SpaceEvenly) { repeat(8) { Box(Modifier.size(8.dp).background(Brass, RoundedCornerShape(3.dp))) } }
    }
}

@Composable
private fun PageSheet(items: List<Story>, modifier: Modifier = Modifier, pageNumber: Int, onOpen: (String) -> Unit) {
    Box(modifier
        .padding(horizontal = 1.dp)
        .shadow(4.dp, RoundedCornerShape(topStart = 6.dp, topEnd = 13.dp, bottomEnd = 4.dp, bottomStart = 6.dp))
        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 13.dp, bottomEnd = 4.dp, bottomStart = 6.dp))
        .background(PaperLight)
        .padding(horizontal = 19.dp, vertical = 15.dp)) {
        Column(Modifier.fillMaxSize()) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("VOL. I    •    FIELDNOTES", color = Brass, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Spacer(Modifier.weight(1f)); Icon(Icons.Default.AutoStories, null, tint = Color(0xFFB9A17B), modifier = Modifier.size(14.dp))
            }
            Spacer(Modifier.height(9.dp)); HorizontalDivider(color = Color(0xFFD9C8A8), thickness = 1.dp)
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                items(items) { story ->
                    Column(Modifier.fillMaxWidth().clickable { onOpen(story.url) }.padding(vertical = 14.dp)) {
                        Text(story.tag, color = Moss, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                        Spacer(Modifier.height(5.dp)); Text(story.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, color = Ink, fontSize = 19.sp, lineHeight = 23.sp, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(6.dp)); Text(story.summary, fontFamily = FontFamily.Serif, fontSize = 12.sp, lineHeight = 17.sp, color = Color(0xFF554A3C), maxLines = 3, overflow = TextOverflow.Ellipsis)
                        Spacer(Modifier.height(7.dp)); Row(verticalAlignment = Alignment.CenterVertically) { Text(story.source, color = Leather, fontSize = 9.sp, fontWeight = FontWeight.Bold); Text("  ·  ${story.time}", color = Muted, fontSize = 9.sp); Spacer(Modifier.weight(1f)); Icon(Icons.Default.BookmarkBorder, "Save article", tint = Brass, modifier = Modifier.size(17.dp)) }
                        Spacer(Modifier.height(10.dp)); HorizontalDivider(color = Color(0xFFE5D8C0), thickness = 0.7.dp)
                    }
                }
            }
            HorizontalDivider(color = Color(0xFFD9C8A8), thickness = 1.dp); Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("QUILLFOLD   •   READ SLOWLY, THINK DEEPLY", color = Muted, fontSize = 7.sp, letterSpacing = 0.7.sp, modifier = Modifier.weight(1f))
                Text("$pageNumber", color = Leather, fontFamily = FontFamily.Serif, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FeedSettings(onClose: () -> Unit) {
    var url by remember { mutableStateOf("") }
    var added by remember { mutableStateOf(false) }
    Box(Modifier.fillMaxSize().background(Color(0xAA20170F)), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 26.dp).clip(RoundedCornerShape(12.dp)).background(PaperLight).padding(22.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) { Text("Your field notes", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 25.sp, color = Ink, modifier = Modifier.weight(1f)); IconButton(onClick = onClose) { Icon(Icons.Default.Close, "Close", tint = Leather) } }
            Text("Choose the journals on your shelf.", color = Muted, fontFamily = FontFamily.Serif, fontSize = 13.sp); Spacer(Modifier.height(14.dp))
            LazyColumn(Modifier.weight(1f).heightIn(max = 300.dp)) {
                items(feedSources) { name -> Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.RssFeed, null, tint = Moss, modifier = Modifier.size(18.dp)); Spacer(Modifier.width(10.dp)); Text(name, color = Ink, fontSize = 13.sp, modifier = Modifier.weight(1f)); Icon(Icons.Default.CheckCircle, null, tint = Moss, modifier = Modifier.size(17.dp)) } }
            }
            if (added) Text("Feed added to your shelf", color = Moss, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
            Spacer(Modifier.height(12.dp)); OutlinedTextField(value = url, onValueChange = { url = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Paste an RSS feed address") }, singleLine = true, shape = RoundedCornerShape(8.dp))
            Spacer(Modifier.height(10.dp)); Button(onClick = { if (url.startsWith("https://") || url.startsWith("http://")) added = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Add to shelf") }
        }
    }
}
