# -*- coding: utf-8 -*-
# Created by Jonathan Brachthäuser <http://b-studios.de/>
#
# Modified by Yufei Cai <caiyufei@informatik.uni-tuebingen.de>
#
#
# Rocco (0.8.2)
# -------------
# If you are having trouble with redcarpet and rocco, 
# saying "uninitialized constant Object::Markdown" or
# similiar than its an issue with the new version of
# redcarpet.
# In that case try this (sudo as necessary):
#
#   gem uninstall redcarpet
#   gem install redcarpet -v 1.17.2
#
#
require 'rocco'
require 'fileutils'

# Custom Comment Styles
# ---------------------
# Monkeypatch CommentStyles for our use of comments. Usually rocco 
# uses C-Style Comments for Scala. These are
#
#     /** Multi - note the double star */ and
#     // Singleline comment
#
# I removed the singleline comments, so we can use them as "in-code" 
# documentation, which actually stays in the code and does not affect 
# the literate text. This makes the text a little more readable, since 
# the reader is not disturbed with "out of context" information.
class Rocco
  module CommentStyles
    COMMENT_STYLES["scala"] = { 
      :single => nil, 
      :multi  => { :start => "/*", :middle => "*", :end => "*/" },
      :heredoc => nil
    }
    COMMENT_STYLES["haskell"] = { 
      :single => nil, 
      :multi  => { :start => "{-", :middle => nil, :end => "-}" },
      :heredoc => nil
    }
  end
end

# Directory Structure
# -------------------
# This configuration is based on the assumption that the directory 
# layout follows this conventions:
#
#     LectureDirectory
#      |- THIS_FILE.rb
#      |
#      |-+ 1-hello
#      | |-- whatever/path/to/Anything.scala
#      |
#      +-+ layout
#      | |-- layout.moustache
#      |
#      |-+ stylesheets
#      | |-- style.css
#
# This directory structure is based on this [guide][GP]
#
# There are two different template files in `gh-pages/_layout`.
# The first one `layout.mustache` is used by rocco to generate
# documentation content.
#
# [Jekyll][JK] is a static site generator used by github to create
# sites like blog posts etc.
#
# The stylesheet is created by using scss a preprocessor for
# cascaded stylesheets. More info can be found on [this page][SS].
#
#
# [GP]: https://gist.github.com/833223
# [JK]: https://github.com/mojombo/jekyll
# [SS]: http://sass-lang.com
require_relative 'exercises.rb'

HERE = File.expand_path(File.dirname(__FILE__))

HTML_DIR = 'html'

ROCCO_OPTS = {
  :language      => "scala",
  :template_file => "#{HERE}/layout/layout.mustache",
  :stylesheet    => "this-gets-ignored-by-rocco?",
}

def get_sheet_path(exercise, sheet_file)
  File.join(HERE, exercise, sheet_file)
end

# TODO:
# READ THIS: https://github.com/mustache/mustache
# THEN FIX layout.moustache

def get_output_path(exercise, sheet_name)
  File.join(HERE, exercise, HTML_DIR, "#{sheet_name}.html")
end

def get_output_link(exercise, sheet_name)
  File.join(HERE, exercise, HTML_DIR, "#{sheet_name}.html")
  # produces wrong path
end

# create documentation from lecturenotes using rocco
EXERCISES.each do |exercise, sheets|
  resolved_sheets = sheets.map do |sheet_name, sheet_file|
    [ sheet_name,
      get_sheet_path(exercise, sheet_file),
      get_output_path(exercise, sheet_name),
      get_output_link(exercise, sheet_name) ]
  end

  sources = resolved_sheets.map(&:last)

  resolved_sheets.each do |sheet_name, scala_file, output_file, link|
    rocco = Rocco.new(scala_file, sources, ROCCO_OPTS)

    FileUtils.mkdir_p File.dirname(output_file)

    File.open(output_file, 'wb') {|fd| fd.write(rocco.to_html)}
    cmd = "git add '#{output_file}'"
    puts cmd
    system cmd
  end
end
