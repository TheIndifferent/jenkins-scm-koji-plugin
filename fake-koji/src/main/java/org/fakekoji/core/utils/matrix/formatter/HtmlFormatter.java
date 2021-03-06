package org.fakekoji.core.utils.matrix.formatter;

import org.fakekoji.core.utils.matrix.cell.CellGroup;
import org.fakekoji.core.utils.matrix.cell.MultiUrlCell;
import org.fakekoji.core.utils.matrix.cell.TitleCell;
import org.fakekoji.core.utils.matrix.cell.UpperCornerCell;
import org.fakekoji.core.utils.matrix.cell.UrlCell;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HtmlFormatter implements Formatter {
    private final boolean expandNames;
    private final String[] projects;

    public HtmlFormatter(boolean expandNames, String[] projects) {
        this.expandNames = expandNames;
        this.projects = projects;
    }

    @Override
    public String rowStart() {
        return "<tr>";
    }

    @Override
    public String rowEnd() {
        return "</tr>\n";
    }

    @Override
    public String tableStart() {
        return "<table class=\"resultsTable\" >\n";
    }

    @Override
    public String tableEnd() {
        return "</table>";
    }

    @Override
    public String edge(final TitleCell cell, final int span) {
        return renderTableCell(cell.getTitle());
    }

    @Override
    public String cells(CellGroup cellGroup, int maxInColumn, final String rowTitle, final String colTitle) {
        final String content;
        if (cellGroup.isEmpty()) {
            content = "0";
        } else {
            content = renderCells(cellGroup, rowTitle, colTitle);
        }
        return renderTableCell(content);
    }

    String expandIfNeeded(int counter, String fullTitle, String rowTitle, String columnTitle) {
        if (!expandNames) {
            return "" + counter;
        }
        String[] rowParts = rowTitle.split("\\?");
        String[] colParts = columnTitle.split("\\?");
        if (rowParts.length == 1 && colParts.length == 1) {
            return "" + counter;
        }
        fullTitle = removePartsFromFullName(fullTitle, rowParts);
        fullTitle = removePartsFromFullName(fullTitle, colParts);
        return fullTitle;
    }

    private String removePartsFromFullName(String fullTitle, String[] parts) {
        if (parts.length > 1) {
            for (String part : parts) {
                part = part.replaceAll("^[^0-9a-zA-Z]+", "");
                part = part.replaceAll("[^0-9a-zA-Z]+$", "");
                //this is  abit buggy, may remove platform from both build and run...
                fullTitle = fullTitle.replace(part, "?");
            }
        }
        fullTitle = fullTitle.replaceAll("[^0-9a-zA-Z]{2,}", "?");
        fullTitle = fullTitle.replaceAll("^[^0-9a-zA-Z]+", "");
        fullTitle = fullTitle.replaceAll("[^0-9a-zA-Z]+$", "");
        return fullTitle;
    }

    @Override
    public String upperCorner(final UpperCornerCell cell) {
        final List<UrlCell> projectCells = cell.projectCells();
        if (projectCells.isEmpty()) {
            return renderHtmlCell("-", "#");
        }
        if (projectCells.size() == 1) {
            final UrlCell projectCell = projectCells.get(0);
            return renderHtmlCell(projectCell.getTitle(), projectCell.getUrl().orElse("#"));
        }
        return renderHtmlCell(projectCells.stream()
                .map(projectCell -> renderHtmlAnchor(projectCell.getTitle(), projectCell.getUrl().orElse("#")))
                .collect(Collectors.joining(" ")));
    }

    String renderCells(
            final CellGroup cellGroup,
            final String rowTitle,
            final String colTitle
    ) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < cellGroup.size(); i++) {
            final TitleCell cell = cellGroup.getCells().get(i);
            final String title = "[" + expandIfNeeded(i + 1, cell.getTitle(), rowTitle, colTitle) + "]";
            final String content;
            if (cell instanceof UrlCell) {
                content = cell(cell.getTitle(), renderHtmlAnchor(title, ((UrlCell) cell).getUrl().orElse("#")));
            } else if (cell instanceof MultiUrlCell) {
                content = renderMultiUrlCell(title, (MultiUrlCell) cell);
            } else {
                content = cell(cell.getTitle(), title);
            }
            stringBuilder.append(content);
        }
        return stringBuilder.toString();
    }

    String renderMultiUrlCell(final String title, final MultiUrlCell cell) {
        return cell(title, cell.getUrls()
                .stream()
                .map(url -> renderHtmlAnchor(title, url))
                .collect(Collectors.joining()));
    }

    String cell(final String cellTitle, final String content) {
        return content;
    }

    String renderHtmlCell(final String body) {
        return "<td>" + body + "</td>";
    }

    private static String blankIfNotHref(String href) {
        if (href.startsWith("#")) {
            return "";
        } else {
            return "target='_blank'";
        }
    }

    String renderHtmlCell(final String body, final String href) {
        return "<td><a " + blankIfNotHref(href) + " href=\"" + href + "\">" + body + "</a></td>";
    }

    String renderHtmlAnchor(final String body, final String href) {
        return "<a " + blankIfNotHref(href) + " href=\"" + href + "\">" + body + "</a>";
    }

    String renderHtmlAnchor(final String body, final String href, final String classes) {
        return "<a " + blankIfNotHref(href) + " class=\"" + classes + "\" href=\"" + href + "\">" + body + "</a>";
    }

    String renderSpan(final String content, final String style) {
        return "<span style=\"" + style + "\">" + content + "</span>";
    }

    String renderTableCell(final String content, String... attributes) {
        String atts = Arrays.stream(attributes).collect(Collectors.joining(" "));
        if (atts.trim().length() > 0) {
            atts = " " + atts;
        }
        return "<td" + atts + ">" + content + "</td>";
    }

    String renderTableCell(final String content, final int span) {
        final String colspan = span != 1
                ? " colspan=\"" + span + "\""
                : "";
        return "<td" + colspan + ">" + content + "</td>";
    }

    @Override
    public String lowerCorner(int found, int all, int span) {
        return renderTableCell(found + "/" + all);
    }
}
